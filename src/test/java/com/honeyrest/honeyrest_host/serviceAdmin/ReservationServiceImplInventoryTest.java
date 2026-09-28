package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoAdmin.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.entity.ReservationStatus;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.repositoryAdmin.PaymentRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationConflictException;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationInventoryGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 관리자 예약 생성/상태 전환 시 객실 행 락 + 겹침 검사(사용자 API 와 같은 규칙)를 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceImplInventoryTest {

    private static final LocalDate IN = LocalDate.of(2026, 10, 1);
    private static final LocalDate OUT = LocalDate.of(2026, 10, 3);

    @Mock private ReservationRepository reservationRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private UserRepository userRepository;
    @Mock private AccommodationRepository accommodationRepository;
    @Mock private SearchCacheInvalidator searchCacheInvalidator;

    private ReservationServiceImpl service;
    private Accommodation accommodation;
    private Room room;
    private User user;

    @BeforeEach
    void setUp() {
        ReservationInventoryGuard guard = new ReservationInventoryGuard(roomRepository, reservationRepository);
        service = new ReservationServiceImpl(reservationRepository, paymentRepository, roomRepository,
                userRepository, new ModelMapper(), accommodationRepository, guard, searchCacheInvalidator);
        accommodation = Accommodation.builder().accommodationId(10L).name("테스트 숙소").build();
        room = Room.builder().roomId(1L).name("디럭스").totalRooms(2).accommodation(accommodation).build();
        user = User.builder().userId(3L).name("홍길동").build();
    }

    private ReservationDTO createForm(String status) {
        return ReservationDTO.builder()
                .userId(3L).roomId(1L).accommodationId(10L)
                .checkInDate(IN).checkOutDate(OUT)
                .guestCount(2).guestName("홍길동").guestPhone("010-0000-0000")
                .price(BigDecimal.valueOf(100000))
                .status(status)
                .build();
    }

    private void stubCreateLookups() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(accommodationRepository.findById(10L)).thenReturn(Optional.of(accommodation));
    }

    @Test
    void 생성_겹치는_점유예약이_총객실수_이상이면_거절한다() {
        stubCreateLookups();
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlapping(1L, IN, OUT, ReservationStatus.OCCUPYING, null)).thenReturn(2L);

        assertThatThrownBy(() -> service.createReservation(createForm(null)))
                .isInstanceOf(ReservationConflictException.class)
                .hasMessageContaining("남은 객실이 없습니다");
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void 생성_잔여가_있으면_락을_잡고_저장한다() {
        stubCreateLookups();
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlapping(1L, IN, OUT, ReservationStatus.OCCUPYING, null)).thenReturn(1L);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationDTO saved = service.createReservation(createForm(null));

        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(roomRepository).findByIdForUpdate(1L);
        verify(searchCacheInvalidator).bumpAfterCommit(); // 점유 예약 생성 → 검색 캐시 세대 증가
    }

    @Test
    void 생성_취소상태로_등록하면_재고검사를_하지_않는다() {
        stubCreateLookups();
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        service.createReservation(createForm("CANCELLED"));

        verify(roomRepository, never()).findByIdForUpdate(anyLong());
        verify(searchCacheInvalidator, never()).bumpAfterCommit(); // 비점유 → 재고 변화 없음
    }

    @Test
    void 상태전환_취소에서_확정으로_바꿀때_만실이면_거절한다() {
        Reservation cancelled = Reservation.builder()
                .reservationId(99L).room(room).accommodation(accommodation).user(user)
                .checkInDate(IN).checkOutDate(OUT).status(ReservationStatus.CANCELLED)
                .build();
        when(reservationRepository.findById(99L)).thenReturn(Optional.of(cancelled));
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlapping(1L, IN, OUT, ReservationStatus.OCCUPYING, 99L)).thenReturn(2L);

        ReservationDTO dto = ReservationDTO.builder().reservationId(99L).status("CONFIRMED").build();

        assertThatThrownBy(() -> service.updateReservation(dto))
                .isInstanceOf(ReservationConflictException.class);
        assertThat(cancelled.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void 상태전환_점유상태끼리_기간변경이_없으면_재고검사를_하지_않는다() {
        Reservation pending = Reservation.builder()
                .reservationId(99L).room(room).accommodation(accommodation).user(user)
                .checkInDate(IN).checkOutDate(OUT).status(ReservationStatus.PENDING)
                .build();
        when(reservationRepository.findById(99L)).thenReturn(Optional.of(pending));

        service.updateReservation(ReservationDTO.builder().reservationId(99L).status("CONFIRMED").build());

        assertThat(pending.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(reservationRepository, never()).countOverlapping(anyLong(), any(), any(), any(), eq(99L));
        verify(searchCacheInvalidator, never()).bumpAfterCommit(); // 점유 → 점유, 기간 동일
    }

    @Test
    void 취소는_객실수를_늘리지_않고_상태만_바꾼다() {
        Reservation confirmed = Reservation.builder()
                .reservationId(5L).room(room).accommodation(accommodation)
                .checkInDate(IN).checkOutDate(OUT).status(ReservationStatus.CONFIRMED)
                .build();
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(confirmed));

        service.cancelReservation(5L, "고객 요청");

        assertThat(confirmed.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(room.getTotalRooms()).isEqualTo(2);
        verify(searchCacheInvalidator).bumpAfterCommit(); // 취소 → 재고 복구 → 검색 캐시 세대 증가
    }
}
