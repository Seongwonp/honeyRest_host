package com.honeyrest.honeyrest_host.serviceCommon;

import com.honeyrest.domain.entity.Accommodation;
import com.honeyrest.domain.entity.Reservation;
import com.honeyrest.domain.entity.ReservationChanges;
import com.honeyrest.domain.entity.Room;
import com.honeyrest.domain.entity.User;
import com.honeyrest.domain.type.ReservationStatus;
import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.serviceCommon.refund.ReservationRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 기존 예약 수정(필드 + 상태 전이)의 공통 규칙. 관리자(serviceAdmin.ReservationServiceImpl)와
 * 오너(serviceOwner.OReservationService) 두 화면 계층이 함께 쓴다.
 * <p>
 * 과거에는 두 계층이 점유 전이 판단(enteringOccupying/occupyingChanged/leavingOccupying)을 복사해 두었고,
 * 오너는 엔티티를 통째로 새로 만들어 save(덮어쓰기)했으며 체크인 &lt; 체크아웃 검증도 관리자에만 있었다.
 * 상태는 {@code Reservation.update} 가 아무 값이나 대입해 전이 표({@link ReservationStatus#canTransition})를 우회했다.
 * <p>
 * 사용 순서 (락 순서 규칙 때문에 두 단계로 나뉜다):
 * <ol>
 *   <li>예약을 잠금 읽기로 가져온다 ({@code findByIdForUpdate}).</li>
 *   <li>{@link #prepare} — 목표 상태 정규화, 날짜·전이 검증, 필요하면 객실 락 + 재고 검사.
 *       연관 엔티티(회원/객실/숙소)를 일반 SELECT 로 읽기 <b>전에</b> 호출해야 재고 검사가 옛 스냅샷을 보지 않는다.</li>
 *   <li>연관 엔티티를 읽는다.</li>
 *   <li>{@link #apply} — 필드는 도메인 {@code update}, 상태는 도메인 {@code changeStatus} 로 반영하고
 *       취소로 들어가면 환불, 점유가 바뀌면 검색 캐시 무효화.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class ReservationModificationService {

    private final ReservationInventoryGuard inventoryGuard;
    private final SearchCacheInvalidator searchCacheInvalidator;
    private final ReservationRefundService refundService;

    /** {@link #prepare} 결과. {@link #apply} 에 그대로 넘긴다. */
    public record Plan(String fromStatus, String toStatus,
                       boolean enteringOccupying, boolean leavingOccupying, boolean occupyingChanged) {

        boolean affectsInventory() {
            return enteringOccupying || leavingOccupying || occupyingChanged;
        }

        boolean enteringCancelled() {
            return ReservationStatus.CANCELLED.equals(toStatus)
                    && !ReservationStatus.CANCELLED.equalsIgnoreCase(fromStatus);
        }
    }

    /**
     * @param requestedRoomId  바꿀 객실 ID (null 이면 유지)
     * @param requestedIn      바꿀 체크인 (null 이면 유지)
     * @param requestedOut     바꿀 체크아웃 (null 이면 유지)
     * @param requestedStatus  바꿀 상태 (null/빈 값이면 유지, 대소문자 무시)
     * @throws IllegalArgumentException 허용 목록에 없는 상태, 체크인 ≥ 체크아웃
     * @throws IllegalStateException    전이 표에 없는 상태 변경
     * @throws ReservationConflictException 점유로 들어가거나 점유 중 객실/기간이 바뀌는데 잔여 객실이 없을 때
     */
    public Plan prepare(Reservation reservation, Long requestedRoomId,
                        LocalDate requestedIn, LocalDate requestedOut, String requestedStatus) {
        String fromStatus = reservation.getStatus();
        String toStatus = ReservationStatus.normalize(requestedStatus, fromStatus);

        LocalDate newIn = requestedIn != null ? requestedIn : reservation.getCheckInDate();
        LocalDate newOut = requestedOut != null ? requestedOut : reservation.getCheckOutDate();
        if (newIn != null && newOut != null && !newIn.isBefore(newOut)) {
            throw new IllegalArgumentException("체크인 날짜는 체크아웃보다 이전이어야 합니다.");
        }

        boolean sameStatus = toStatus != null && toStatus.equalsIgnoreCase(fromStatus);
        if (!sameStatus && !ReservationStatus.canTransition(fromStatus, toStatus)) {
            throw new IllegalStateException("예약 상태를 " + fromStatus + " 에서 " + toStatus + " (으)로 바꿀 수 없습니다.");
        }

        Long oldRoomId = reservation.getRoom() != null ? reservation.getRoom().getRoomId() : null;
        Long newRoomId = requestedRoomId != null ? requestedRoomId : oldRoomId;

        boolean wasOccupying = ReservationStatus.isOccupying(fromStatus);
        boolean willOccupy = ReservationStatus.isOccupying(toStatus);
        boolean enteringOccupying = willOccupy && !wasOccupying;
        boolean leavingOccupying = wasOccupying && !willOccupy;
        boolean occupyingChanged = willOccupy
                && (!Objects.equals(oldRoomId, newRoomId)
                    || !Objects.equals(reservation.getCheckInDate(), newIn)
                    || !Objects.equals(reservation.getCheckOutDate(), newOut));

        if (enteringOccupying || occupyingChanged) {
            inventoryGuard.lockRoomAndAssertAvailable(newRoomId, newIn, newOut, reservation.getReservationId());
        }
        return new Plan(fromStatus, toStatus, enteringOccupying, leavingOccupying, occupyingChanged);
    }

    /**
     * 필드 변경(상태 제외)은 도메인 {@code update}, 상태는 도메인 {@code changeStatus} 로 반영한다.
     * 취소(CANCELLED)로 들어가면 같은 트랜잭션에서 환불하고, 점유가 바뀌면 커밋 후 검색 캐시를 무효화한다.
     */
    public void apply(Reservation reservation, Plan plan, ReservationChanges changes,
                      User newUser, Room newRoom, Accommodation newAccommodation) {
        reservation.update(withoutStatus(changes), newUser, newRoom, newAccommodation);
        reservation.changeStatus(plan.toStatus());

        if (plan.enteringCancelled()) {
            refundService.refund(reservation, reservation.getCancelReason());
        }
        if (plan.affectsInventory()) {
            searchCacheInvalidator.bumpAfterCommit();
        }
    }

    /** 상태만 null 로 가린 변경 값. 도메인 update 가 상태를 직접 대입하지 못하게 한다. */
    static ReservationChanges withoutStatus(ReservationChanges c) {
        return new ReservationChanges() {
            @Override public LocalDate getCheckInDate() { return c.getCheckInDate(); }
            @Override public LocalDate getCheckOutDate() { return c.getCheckOutDate(); }
            @Override public Integer getGuestCount() { return c.getGuestCount(); }
            @Override public String getGuestName() { return c.getGuestName(); }
            @Override public String getGuestPhone() { return c.getGuestPhone(); }
            @Override public BigDecimal getPrice() { return c.getPrice(); }
            @Override public String getStatus() { return null; }
            @Override public String getCancelReason() { return c.getCancelReason(); }
            @Override public String getSpecialRequest() { return c.getSpecialRequest(); }
            @Override public String getAccommodationName() { return c.getAccommodationName(); }
        };
    }
}
