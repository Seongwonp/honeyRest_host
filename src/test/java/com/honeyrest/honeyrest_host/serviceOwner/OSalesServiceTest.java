package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.domain.entity.*;
import com.honeyrest.domain.type.ReservationStatus;
import com.honeyrest.honeyrest_host.dtoOwner.DaySalesDTO;
import com.honeyrest.honeyrest_host.dtoOwner.MonthSalesDTO;
import com.honeyrest.honeyrest_host.support.JpaTestFixtures;
import com.honeyrest.honeyrest_host.type.PaymentStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 오너 매출 집계(SQL GROUP BY) 검증 (test 프로필 = H2).
 * 기준: 결제일, 결제 DONE + 예약 미취소, 시작일·종료일 포함.
 * 테스트 DB 는 다른 테스트와 공유되므로(롤백) 먼 과거 날짜 범위를 써서 다른 데이터와 섞이지 않게 한다.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OSalesServiceTest {

    private static final LocalDate START = LocalDate.of(2001, 3, 10);
    private static final LocalDate END = LocalDate.of(2001, 3, 20);

    @Autowired private OSalesService salesService;
    @Autowired private EntityManager em;

    private JpaTestFixtures fx;
    private User user;
    private Room room;
    private Room otherCompanyRoom;
    private Company company;
    private Accommodation accommodation;

    @BeforeEach
    void setUp() {
        fx = new JpaTestFixtures(em);
        company = fx.company("매출 업체");
        accommodation = fx.accommodation(company, "매출 숙소");
        room = fx.room(accommodation, "매출 객실", 5);
        otherCompanyRoom = fx.room(fx.accommodation(fx.company("다른 업체"), "다른 숙소"), "다른 객실", 5);
        user = fx.user("매출고객");
        seed();
    }

    private void paid(Room r, LocalDateTime paidAt, long amount, String paymentStatus, String reservationStatus) {
        Reservation reservation = fx.reservation(user, r, reservationStatus,
                paidAt.toLocalDate().plusDays(30), paidAt.toLocalDate().plusDays(31));
        em.persist(Payment.builder()
                .reservation(reservation).userId(user.getUserId())
                .amount(BigDecimal.valueOf(amount)).paymentMethod("카드")
                .paymentStatus(paymentStatus).pgProvider("TOSS").paymentDate(paidAt)
                .build());
    }

    private void seed() {
        // 포함: 시작일 0시대, 종료일 23시대
        paid(room, START.atTime(0, 30), 10_000, PaymentStatus.DONE, ReservationStatus.CONFIRMED);
        paid(room, START.atTime(15, 0), 5_000, PaymentStatus.DONE, ReservationStatus.COMPLETED);
        paid(room, END.atTime(23, 0), 20_000, PaymentStatus.DONE, ReservationStatus.CONFIRMED);
        // 제외: 범위 밖
        paid(room, START.minusDays(1).atTime(23, 59), 1_000, PaymentStatus.DONE, ReservationStatus.CONFIRMED);
        paid(room, END.plusDays(1).atTime(0, 0), 1_000, PaymentStatus.DONE, ReservationStatus.CONFIRMED);
        // 제외: 환불된 결제, 취소된 예약(결제는 DONE 으로 남은 과거 데이터)
        paid(room, START.plusDays(2).atTime(12, 0), 7_000, PaymentStatus.CANCELED, ReservationStatus.CANCELLED);
        paid(room, START.plusDays(2).atTime(12, 0), 8_000, PaymentStatus.DONE, ReservationStatus.CANCELLED);
        // 다른 업체
        paid(otherCompanyRoom, START.plusDays(3).atTime(9, 0), 50_000, PaymentStatus.DONE, ReservationStatus.CONFIRMED);
        fx.flushAndClear();
    }

    @Test
    void 일별_전체매출은_시작일과_종료일을_포함하고_취소_환불은_제외한다() {
        List<DaySalesDTO> days = salesService.getDaySales(START, END);

        assertThat(days).extracting(DaySalesDTO::getDate)
                .containsExactly(START, START.plusDays(3), END);
        DaySalesDTO first = days.get(0);
        assertThat(first.getDayPrice()).isEqualByComparingTo("15000"); // 과거 isAfter 버그로 빠지던 시작일
        assertThat(first.getQuantity()).isEqualTo(2);
        assertThat(days.get(2).getDayPrice()).isEqualByComparingTo("20000");
    }

    @Test
    void 업체와_숙소_범위는_해당_매출만_집계한다() {
        List<DaySalesDTO> companyDays = salesService.getCompanyDaySales(company.getCompanyId(), START, END);
        assertThat(companyDays).extracting(DaySalesDTO::getDate).containsExactly(START, END);

        List<DaySalesDTO> accDays = salesService.getAccommodationDaySales(accommodation.getAccommodationId(), START, END);
        assertThat(accDays).extracting(DaySalesDTO::getDayPrice)
                .usingElementComparator(BigDecimal::compareTo)
                .containsExactly(new BigDecimal("15000"), new BigDecimal("20000"));
    }

    @Test
    void 월별_매출은_같은_기준으로_월마다_합산한다() {
        List<MonthSalesDTO> months = salesService.getCompanyMonthSales(company.getCompanyId(),
                LocalDate.of(2001, 3, 1), LocalDate.of(2001, 3, 31));

        assertThat(months).hasSize(1);
        assertThat(months.get(0).getDate()).isEqualTo(YearMonth.of(2001, 3));
        // 3/9 23:59, 3/21 0:00 도 3월이므로 포함: 15,000 + 20,000 + 1,000 + 1,000
        assertThat(months.get(0).getMonthPrice()).isEqualByComparingTo("37000");
        assertThat(months.get(0).getQuantity()).isEqualTo(5);

        List<MonthSalesDTO> all = salesService.getMonthSales(START, END);
        assertThat(all.get(0).getMonthPrice()).isEqualByComparingTo("85000"); // 35,000 + 다른 업체 50,000
    }
}
