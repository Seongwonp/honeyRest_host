package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.entity.ReservationStatus;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OReservationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationConflictException;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationInventoryGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 오너 화면 예약 등록(기본 PENDING)/수정 시 객실 행 락 + 겹침 검사를 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class OReservationServiceInventoryTest {

    private static final LocalDate IN = LocalDate.of(2026, 10, 1);
    private static final LocalDate OUT = LocalDate.of(2026, 10, 3);

    @Mock private OReservationRepository oReservationRepository;
    @Mock private ORoomRepository oRoomRepository;
    @Mock private OUserRepository userRepository;
    @Mock private OAccommodationRepository accommodationRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private SearchCacheInvalidator searchCacheInvalidator;

    private OReservationService service;
    private Room room;

    @BeforeEach
    void setUp() {
        ReservationInventoryGuard guard = new ReservationInventoryGuard(roomRepository, reservationRepository);
        service = new OReservationService(oReservationRepository, oRoomRepository, userRepository,
                accommodationRepository, guard, searchCacheInvalidator);
        room = Room.builder().roomId(1L).name("디럭스").totalRooms(1).build();
    }

    private ReservationDTO form(Long id, String status) {
        return ReservationDTO.builder()
                .reservationId(id).userId(3L).roomId(1L).accommodationId(10L)
                .accommodationName("테스트 숙소").roomName("디럭스").reservationNumber("HR-TEST")
                .checkInDate(IN).checkOutDate(OUT)
                .guestCount(2).guestName("홍길동").guestPhone("010-0000-0000")
                .price(BigDecimal.valueOf(100000))
                .status(status)
                .build();
    }

    @Test
    void 등록_기본상태_PENDING도_만실이면_거절한다() {
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlapping(1L, IN, OUT, ReservationStatus.OCCUPYING, null)).thenReturn(1L);

        assertThatThrownBy(() -> service.registerReservation(form(null, null)))
                .isInstanceOf(ReservationConflictException.class);
        verify(oReservationRepository, never()).save(any());
    }

    @Test
    void 등록_잔여가_있으면_저장한다() {
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlapping(1L, IN, OUT, ReservationStatus.OCCUPYING, null)).thenReturn(0L);

        service.registerReservation(form(null, null));

        verify(oReservationRepository).save(any(Reservation.class));
    }

    @Test
    void 등록_취소상태는_재고검사_없이_저장한다() {
        service.registerReservation(form(null, "CANCELLED"));

        verify(roomRepository, never()).findByIdForUpdate(anyLong());
        verify(oReservationRepository).save(any(Reservation.class));
    }

    @Test
    void 수정_취소에서_확정으로_전환할때_만실이면_거절한다() {
        Reservation cancelled = Reservation.builder()
                .reservationId(7L).room(room).checkInDate(IN).checkOutDate(OUT)
                .status(ReservationStatus.CANCELLED).build();
        when(oReservationRepository.findById(7L)).thenReturn(Optional.of(cancelled));
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlapping(1L, IN, OUT, ReservationStatus.OCCUPYING, 7L)).thenReturn(1L);

        assertThatThrownBy(() -> service.modifyReservation(form(7L, "CONFIRMED")))
                .isInstanceOf(ReservationConflictException.class);
        verify(oReservationRepository, never()).save(any());
    }

    @Test
    void 수정_점유상태_유지_기간동일이면_재고검사를_하지_않는다() {
        Reservation pending = Reservation.builder()
                .reservationId(7L).room(room).checkInDate(IN).checkOutDate(OUT)
                .status(ReservationStatus.PENDING).build();
        when(oReservationRepository.findById(7L)).thenReturn(Optional.of(pending));

        service.modifyReservation(form(7L, "CONFIRMED"));

        verify(roomRepository, never()).findByIdForUpdate(anyLong());
        verify(oReservationRepository).save(any(Reservation.class));
    }
}
