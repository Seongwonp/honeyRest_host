package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.*;
import com.honeyrest.honeyrest_host.support.JpaTestFixtures;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 관리자 ReservationServiceImpl 통합 테스트 (test 프로필 = H2 인메모리).
 * 과거 버전은 빈 테스트 본문과 실 DB에 하드코딩 ID로 INSERT 하는 테스트였다.
 * 이제 각 테스트가 자체 픽스처를 만들고 트랜잭션 롤백으로 정리한다.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReservationServiceImplTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private EntityManager em;

    private JpaTestFixtures fx;
    private Company company;
    private Room room;
    private User user;

    private final LocalDate in = LocalDate.now().plusDays(10);
    private final LocalDate out = LocalDate.now().plusDays(12);

    @BeforeEach
    void setUp() {
        fx = new JpaTestFixtures(em);
        company = fx.company("테스트 업체");
        Accommodation acc = fx.accommodation(company, "test 숙소");
        room = fx.room(acc, "디럭스", 1);
        user = fx.user("김짱구");
    }

    private ReservationDTO newDto() {
        return ReservationDTO.builder()
                .userId(user.getUserId())
                .roomId(room.getRoomId())
                .guestName("김짱구")
                .guestPhone("010-1234-5678")
                .guestCount(2)
                .price(BigDecimal.valueOf(100000))
                .checkInDate(in)
                .checkOutDate(out)
                .build();
    }

    @Test
    void createReservation_기본상태는_CONFIRMED_이고_숙소정보를_객실에서_채운다() {
        ReservationDTO saved = reservationService.createReservation(newDto());

        assertThat(saved.getReservationId()).isNotNull();
        assertThat(saved.getReservationNumber()).startsWith("HR-");
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(saved.getAccommodationName()).isEqualTo("test 숙소");
        assertThat(saved.getRoomName()).isEqualTo("디럭스");
        assertThat(saved.getOriginalPrice()).isEqualByComparingTo("100000");
    }

    @Test
    void createReservation_재고가_없으면_거부한다() {
        reservationService.createReservation(newDto()); // totalRooms=1 을 모두 점유

        assertThatThrownBy(() -> reservationService.createReservation(newDto()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void createReservation_체크인이_체크아웃보다_늦으면_거부한다() {
        ReservationDTO dto = newDto();
        dto.setCheckInDate(out);
        dto.setCheckOutDate(in);

        assertThatThrownBy(() -> reservationService.createReservation(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getReservationByNumber_와_getReservationById_는_같은_예약을_돌려준다() {
        ReservationDTO saved = reservationService.createReservation(newDto());
        fx.flushAndClear();

        ReservationDTO byId = reservationService.getReservationById(saved.getReservationId());
        ReservationDTO byNumber = reservationService.getReservationByNumber(saved.getReservationNumber());

        assertThat(byId.getReservationNumber()).isEqualTo(saved.getReservationNumber());
        assertThat(byId.getUserId()).isEqualTo(user.getUserId());
        assertThat(byNumber.getReservationId()).isEqualTo(saved.getReservationId());
    }

    @Test
    void 존재하지_않는_예약은_EntityNotFoundException() {
        assertThatThrownBy(() -> reservationService.getReservationById(-1L))
                .isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> reservationService.getReservationByNumber("HR-NONE"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateReservation_부분_업데이트와_상태_정규화() {
        ReservationDTO saved = reservationService.createReservation(newDto());

        ReservationDTO patch = ReservationDTO.builder()
                .reservationId(saved.getReservationId())
                .guestName("신짱아")
                .status("completed")
                .build();
        ReservationDTO updated = reservationService.updateReservation(patch);

        assertThat(updated.getGuestName()).isEqualTo("신짱아");
        assertThat(updated.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
        assertThat(updated.getGuestPhone()).isEqualTo("010-1234-5678"); // 미지정 필드는 유지
    }

    @Test
    void cancelReservation_은_CANCELLED_로_바꾸고_재고를_돌려준다() {
        ReservationDTO first = reservationService.createReservation(newDto());

        reservationService.cancelReservation(first.getReservationId(), "고객 요청");
        fx.flushAndClear();

        ReservationDTO cancelled = reservationService.getReservationById(first.getReservationId());
        assertThat(cancelled.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(cancelled.getCancelReason()).isEqualTo("고객 요청");

        // 취소로 재고가 풀렸으므로 같은 기간 재예약이 가능해야 한다
        assertThat(reservationService.createReservation(newDto()).getReservationId()).isNotNull();
        // 이미 취소된 예약의 재취소는 무시(예외 없음)
        reservationService.cancelReservation(first.getReservationId(), "중복");
    }

    @Test
    void getCompanyReservations_는_상태로_필터링한다() {
        fx.reservation(user, room, ReservationStatus.CONFIRMED, in, out);
        fx.reservation(user, room, ReservationStatus.CANCELLED, in, out);
        fx.reservation(user, room, ReservationStatus.CANCELLED, in.plusDays(5), out.plusDays(5));
        fx.flushAndClear();

        PageRequestDTO pr = PageRequestDTO.builder().page(1).size(10).build();
        PageResponseDTO<ReservationDTO> cancelled =
                reservationService.getCompanyReservations(company.getCompanyId(), "cancelled", null, pr);
        PageResponseDTO<ReservationDTO> all =
                reservationService.getCompanyReservations(company.getCompanyId(), "ALL", null, pr);

        assertThat(cancelled.getTotal()).isEqualTo(2);
        assertThat(cancelled.getDtoList()).allMatch(d -> ReservationStatus.CANCELLED.equals(d.getStatus()));
        assertThat(all.getTotal()).isEqualTo(3);

        assertThatThrownBy(() -> reservationService.getCompanyReservations(company.getCompanyId(), "WRONG", null, pr))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
