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
 * 예약 수정(필드 + 상태 전이) 공통 규칙. 관리자·오너 화면이 함께 쓴다.
 * 락 순서 때문에 잠금 읽기 → {@link #prepare}(검증·재고 검사) → 연관 엔티티 조회 → {@link #apply} 순으로 호출한다.
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
