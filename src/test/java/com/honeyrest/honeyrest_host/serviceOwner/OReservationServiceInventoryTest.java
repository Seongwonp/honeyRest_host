package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
import com.honeyrest.domain.entity.Accommodation;
import com.honeyrest.domain.entity.Reservation;
import com.honeyrest.domain.entity.User;
import com.honeyrest.domain.type.ReservationStatus;
import com.honeyrest.domain.entity.Room;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OReservationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationConflictException;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationInventoryGuard;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationModificationService;
import com.honeyrest.honeyrest_host.serviceCommon.refund.ReservationRefundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
 * 오너 화면 예약 등록(기본 PENDING)/수정 시 객실 행 락 + 겹침 검사, 그리고 수정이 기존 엔티티에
 * 도메인 메서드로 반영되는지(덮어쓰기 save 없음, 전이 표 검사)를 검증한다.
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
    @Mock private ReservationRefundService refundService;

    private OReservationService service;
    private Room room;
    private Accommodation accommodation;
    private User user;

    @BeforeEach
    void setUp() {
        ReservationInventoryGuard guard = new ReservationInventoryGuard(roomRepository, reservationRepository);
        ReservationModificationService modification =
                new ReservationModificationService(guard, searchCacheInvalidator, refundService);
        service = new OReservationService(oReservationRepository, oRoomRepository, userRepository,
                accommodationRepository, guard, modification, searchCacheInvalidator);
        accommodation = Accommodation.builder().accommodationId(10L).name("테스트 숙소").build();
        room = Room.builder().roomId(1L).name("디럭스").totalRooms(1).accommodation(accommodation).build();
        user = User.builder().userId(3L).name("홍길동").build();
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

    private Reservation existing(String status) {
        return Reservation.builder()
                .reservationId(7L).room(room).accommodation(accommodation).user(user)
                .reservationNumber("HR-TEST").guestName("홍길동").guestPhone("010-0000-0000").guestCount(2)
                .price(BigDecimal.valueOf(100000))
                .checkInDate(IN).checkOutDate(OUT)
                .status(status).build();
    }

    @Test
    void 등록_기본상태_PENDING도_만실이면_거절한다() {
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlappingForUpdate(1L, IN, OUT, ReservationStatus.OCCUPYING, null)).thenReturn(1L);

        assertThatThrownBy(() -> service.registerReservation(form(null, null)))
                .isInstanceOf(ReservationConflictException.class);
        verify(oReservationRepository, never()).save(any());
    }

    @Test
    void 등록_잔여가_있으면_저장한다() {
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlappingForUpdate(1L, IN, OUT, ReservationStatus.OCCUPYING, null)).thenReturn(0L);

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
    void 등록_체크인이_체크아웃보다_늦으면_거절한다() {
        ReservationDTO dto = form(null, "CANCELLED");
        dto.setCheckInDate(OUT);
        dto.setCheckOutDate(IN);

        assertThatThrownBy(() -> service.registerReservation(dto)).isInstanceOf(IllegalArgumentException.class);
        verify(oReservationRepository, never()).save(any());
    }

    @Test
    void 수정_취소에서_확정으로_전환할때_만실이면_거절한다() {
        Reservation cancelled = existing(ReservationStatus.CANCELLED);
        when(oReservationRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(cancelled));
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlappingForUpdate(1L, IN, OUT, ReservationStatus.OCCUPYING, 7L)).thenReturn(1L);

        assertThatThrownBy(() -> service.modifyReservation(form(7L, "CONFIRMED")))
                .isInstanceOf(ReservationConflictException.class);
        assertThat(cancelled.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(oReservationRepository, never()).save(any());
    }

    @Test
    void 수정은_기존_엔티티에_도메인_메서드로_반영하고_덮어쓰기_저장하지_않는다() {
        Reservation pending = existing(ReservationStatus.PENDING);
        when(oReservationRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(pending));

        ReservationDTO dto = form(7L, "confirmed");
        dto.setGuestName("김철수");
        service.modifyReservation(dto);

        assertThat(pending.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(pending.getGuestName()).isEqualTo("김철수");
        verify(roomRepository, never()).findByIdForUpdate(anyLong()); // 점유 유지 + 기간 동일 → 재고 검사 없음
        verify(oReservationRepository, never()).save(any());
        verify(searchCacheInvalidator, never()).bumpAfterCommit();
    }

    @Test
    void 수정_전이표에_없는_상태변경은_거절한다() {
        Reservation completed = existing(ReservationStatus.COMPLETED);
        when(oReservationRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(completed));

        // COMPLETED → PENDING 은 허용되지 않는다 (과거에는 save 덮어쓰기로 무엇이든 가능했다)
        assertThatThrownBy(() -> service.modifyReservation(form(7L, "PENDING")))
                .isInstanceOf(IllegalStateException.class);
        assertThat(completed.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
    }

    @Test
    void 수정_체크인이_체크아웃보다_늦으면_거절한다() {
        when(oReservationRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(existing(ReservationStatus.CONFIRMED)));
        ReservationDTO dto = form(7L, "CONFIRMED");
        dto.setCheckInDate(OUT);
        dto.setCheckOutDate(IN);

        assertThatThrownBy(() -> service.modifyReservation(dto)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 수정으로_취소하면_환불하고_검색캐시를_무효화한다() {
        Reservation confirmed = existing(ReservationStatus.CONFIRMED);
        when(oReservationRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(confirmed));
        ReservationDTO dto = form(7L, "CANCELLED");
        dto.setCancelReason("고객 요청");

        service.modifyReservation(dto);

        assertThat(confirmed.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(refundService).refund(eq(confirmed), eq("고객 요청"));
        verify(searchCacheInvalidator).bumpAfterCommit();
    }
}
