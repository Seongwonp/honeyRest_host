package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoAdmin.ReservationDTO;
import com.honeyrest.domain.entity.Accommodation;
import com.honeyrest.domain.entity.Reservation;
import com.honeyrest.domain.type.ReservationStatus;
import com.honeyrest.domain.entity.Room;
import com.honeyrest.domain.entity.User;
import com.honeyrest.honeyrest_host.repositoryAdmin.PaymentRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationConflictException;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationInventoryGuard;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationModificationService;
import com.honeyrest.honeyrest_host.serviceCommon.refund.ReservationRefundService;
import org.mockito.InOrder;
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
import static org.mockito.Mockito.inOrder;
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
    @Mock private ReservationRefundService refundService;

    private ReservationServiceImpl service;
    private Accommodation accommodation;
    private Room room;
    private User user;

    @BeforeEach
    void setUp() {
        ReservationInventoryGuard guard = new ReservationInventoryGuard(roomRepository, reservationRepository);
        ReservationModificationService modification =
                new ReservationModificationService(guard, searchCacheInvalidator, refundService);
        service = new ReservationServiceImpl(reservationRepository, paymentRepository, roomRepository,
                userRepository, new ModelMapper(), accommodationRepository, guard, modification, refundService,
                searchCacheInvalidator);
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

    /** 점유 상태 생성은 객실을 락(findByIdForUpdate)으로 읽으므로 일반 findById 를 쓰지 않는다. */
    private void stubCreateLookups(boolean occupying) {
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        if (!occupying) {
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        }
        when(accommodationRepository.findById(10L)).thenReturn(Optional.of(accommodation));
    }

    @Test
    void 생성_겹치는_점유예약이_총객실수_이상이면_거절한다() {
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlappingForUpdate(1L, IN, OUT, ReservationStatus.OCCUPYING, null)).thenReturn(2L);

        assertThatThrownBy(() -> service.createReservation(createForm(null)))
                .isInstanceOf(ReservationConflictException.class)
                .hasMessageContaining("남은 객실이 없습니다");
        verify(reservationRepository, never()).save(any());
        // 락 순서: 재고 검사가 거절되면 회원/숙소 일반 SELECT 는 아예 실행되지 않는다
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void 생성은_객실락과_잠금읽기_겹침수를_회원_숙소_조회보다_먼저_한다() {
        stubCreateLookups(true);
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlappingForUpdate(1L, IN, OUT, ReservationStatus.OCCUPYING, null)).thenReturn(0L);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        service.createReservation(createForm(null));

        InOrder order = inOrder(roomRepository, reservationRepository, userRepository, accommodationRepository);
        order.verify(roomRepository).findByIdForUpdate(1L);
        order.verify(reservationRepository).countOverlappingForUpdate(1L, IN, OUT, ReservationStatus.OCCUPYING, null);
        order.verify(userRepository).findById(3L);
        order.verify(accommodationRepository).findById(10L);
    }

    @Test
    void 생성_잔여가_있으면_락을_잡고_저장한다() {
        stubCreateLookups(true);
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlappingForUpdate(1L, IN, OUT, ReservationStatus.OCCUPYING, null)).thenReturn(1L);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationDTO saved = service.createReservation(createForm(null));

        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(roomRepository).findByIdForUpdate(1L);
        verify(searchCacheInvalidator).bumpAfterCommit(); // 점유 예약 생성 → 검색 캐시 세대 증가
    }

    @Test
    void 생성_취소상태로_등록하면_재고검사를_하지_않는다() {
        stubCreateLookups(false);
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
        when(reservationRepository.findByIdForUpdate(99L)).thenReturn(Optional.of(cancelled));
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlappingForUpdate(1L, IN, OUT, ReservationStatus.OCCUPYING, 99L)).thenReturn(2L);

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
        when(reservationRepository.findByIdForUpdate(99L)).thenReturn(Optional.of(pending));

        service.updateReservation(ReservationDTO.builder().reservationId(99L).status("CONFIRMED").build());

        assertThat(pending.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(reservationRepository, never()).countOverlappingForUpdate(anyLong(), any(), any(), any(), eq(99L));
        verify(searchCacheInvalidator, never()).bumpAfterCommit(); // 점유 → 점유, 기간 동일
    }

    @Test
    void 취소는_객실수를_늘리지_않고_상태만_바꾼다() {
        Reservation confirmed = Reservation.builder()
                .reservationId(5L).room(room).accommodation(accommodation)
                .checkInDate(IN).checkOutDate(OUT).status(ReservationStatus.CONFIRMED)
                .build();
        when(reservationRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(confirmed));

        service.cancelReservation(5L, "고객 요청");

        assertThat(confirmed.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(room.getTotalRooms()).isEqualTo(2);
        verify(searchCacheInvalidator).bumpAfterCommit(); // 취소 → 재고 복구 → 검색 캐시 세대 증가
        verify(refundService).refund(confirmed, "고객 요청"); // 취소는 환불을 동반한다
    }

    @Test
    void 이미_취소된_예약의_재취소는_환불을_다시_호출하지_않는다() {
        Reservation cancelled = Reservation.builder()
                .reservationId(5L).room(room).accommodation(accommodation)
                .checkInDate(IN).checkOutDate(OUT).status(ReservationStatus.CANCELLED)
                .build();
        when(reservationRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(cancelled));

        service.cancelReservation(5L, "중복");

        verify(refundService, never()).refund(any(), any());
        verify(searchCacheInvalidator, never()).bumpAfterCommit();
    }

    @Test
    void 취소요청_승인은_도메인_전이로_바꾸고_환불한_뒤_갱신된_DTO를_돌려준다() {
        Reservation requested = Reservation.builder()
                .reservationId(6L).room(room).accommodation(accommodation).user(user)
                .checkInDate(IN).checkOutDate(OUT).status(ReservationStatus.CANCEL_REQUEST).cancelReason("일정 변경")
                .build();
        when(reservationRepository.findByIdForUpdate(6L)).thenReturn(Optional.of(requested));

        ReservationDTO updated = service.approveCancelRequest(6L, null);

        assertThat(updated).isNotNull();
        assertThat(updated.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(updated.getCancelReason()).isEqualTo("일정 변경"); // 사유 null 이면 기존 사유 유지
        verify(refundService).refund(requested, "일정 변경");
        verify(searchCacheInvalidator).bumpAfterCommit();
    }

    @Test
    void 취소요청이_아닌_예약의_승인_거부_노쇼_완료는_전이표에_따라_거절된다() {
        Reservation completed = Reservation.builder()
                .reservationId(8L).room(room).accommodation(accommodation)
                .checkInDate(IN).checkOutDate(OUT).status(ReservationStatus.COMPLETED)
                .build();
        when(reservationRepository.findByIdForUpdate(8L)).thenReturn(Optional.of(completed));

        assertThatThrownBy(() -> service.approveCancelRequest(8L, null)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> service.rejectCancelRequest(8L, "x")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> service.markNoShow(8L)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> service.markCompleted(8L)).isInstanceOf(IllegalStateException.class);
        assertThat(completed.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
        verify(refundService, never()).refund(any(), any());
    }

    @Test
    void 거부_완료_노쇼는_도메인_메서드로_전이한다() {
        Reservation requested = Reservation.builder().reservationId(1L).status(ReservationStatus.CANCEL_REQUEST).build();
        Reservation confirmed = Reservation.builder().reservationId(2L).status(ReservationStatus.CONFIRMED).build();
        Reservation pending = Reservation.builder().reservationId(3L).status(ReservationStatus.PENDING).build();
        when(reservationRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(requested));
        when(reservationRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(confirmed));
        when(reservationRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(pending));

        service.rejectCancelRequest(1L, "규정상 불가");
        service.markCompleted(2L);
        service.markNoShow(3L);

        assertThat(requested.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(requested.getCancelReason()).isEqualTo("[REJECT] 규정상 불가");
        assertThat(confirmed.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
        assertThat(pending.getStatus()).isEqualTo(ReservationStatus.NO_SHOW);
        verify(searchCacheInvalidator, never()).bumpAfterCommit(); // 점유 → 점유
    }
}
