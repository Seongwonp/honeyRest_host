package com.honeyrest.honeyrest_host.serviceCommon;

import com.honeyrest.honeyrest_host.entity.ReservationStatus;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 객실 재고(겹침) 검사.
 * <p>
 * 재고의 기준은 {@code room.total_rooms − [checkIn, checkOut) 과 겹치는 점유 상태 예약 수} 이다.
 * 사용자 저장소의 ReserveService.createReservation 과 같은 규칙을 쓴다:
 * 객실 행을 PESSIMISTIC_WRITE 로 잠근 뒤 겹치는 점유 예약 수가 totalRooms 이상이면 거절한다.
 * 반드시 호출자의 트랜잭션 안에서 호출해야 락이 커밋 시점까지 유지된다.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class ReservationInventoryGuard {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    /**
     * @param excludeReservationId 기존 예약을 수정할 때 자기 자신을 겹침 계산에서 빼기 위한 ID (신규 생성이면 null)
     * @return 락이 걸린 Room
     * @throws ReservationConflictException 잔여 객실이 없을 때
     */
    public Room lockRoomAndAssertAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut,
                                           Long excludeReservationId) {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId는 필수입니다.");
        }
        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("체크인 날짜는 체크아웃보다 이전이어야 합니다.");
        }

        Room room = roomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new EntityNotFoundException("객실을 찾을 수 없습니다. roomId=" + roomId));

        int totalRooms = room.getTotalRooms() == null ? 0 : room.getTotalRooms();
        long occupied = reservationRepository.countOverlapping(
                roomId, checkIn, checkOut, ReservationStatus.OCCUPYING, excludeReservationId);

        if (occupied >= totalRooms) {
            log.info("재고 부족으로 예약 거절: roomId={}, checkIn={}, checkOut={}, occupied={}, totalRooms={}",
                    roomId, checkIn, checkOut, occupied, totalRooms);
            throw new ReservationConflictException(
                    "선택한 기간(" + checkIn + " ~ " + checkOut + ")에 남은 객실이 없습니다. "
                            + "(예약 " + occupied + "건 / 총 " + totalRooms + "실)");
        }
        return room;
    }
}
