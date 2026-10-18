package com.honeyrest.honeyrest_host.serviceCommon;

import com.honeyrest.domain.entity.*;
import com.honeyrest.domain.type.ReservationStatus;
import com.honeyrest.honeyrest_host.serviceAdmin.ReservationService;
import com.honeyrest.honeyrest_host.serviceCommon.refund.RefundFailedException;
import com.honeyrest.honeyrest_host.serviceCommon.refund.ReservationRefundService;
import com.honeyrest.honeyrest_host.serviceCommon.refund.TossCancelClient;
import com.honeyrest.honeyrest_host.support.JpaTestFixtures;
import com.honeyrest.honeyrest_host.type.PaymentStatus;
import com.honeyrest.honeyrest_host.type.PointHistoryType;
import com.honeyrest.honeyrest_host.type.UserCouponStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 호스트 예약 취소/취소 승인 시 환불(결제 취소·포인트 환불·쿠폰 복원)과 멱등성 (test 프로필 = H2).
 * 토스 호출은 TossCancelClient 를 목으로 바꿔 검증한다.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReservationRefundIntegrationTest {

    private static final int USED_POINT = 3000;
    private static final int BALANCE_AFTER_USE = 1000;

    @Autowired private ReservationService reservationService;
    @Autowired private ReservationRefundService refundService;
    @Autowired private EntityManager em;
    @MockitoBean private TossCancelClient tossCancelClient;

    private JpaTestFixtures fx;
    private User user;
    private Room room;

    @BeforeEach
    void setUp() {
        fx = new JpaTestFixtures(em);
        Company company = fx.company("환불 업체");
        Accommodation acc = fx.accommodation(company, "환불 숙소");
        room = fx.room(acc, "스탠다드", 2);
        user = fx.user("환불고객");
        user.updatePoint(BALANCE_AFTER_USE);
    }

    /** 사용자 API 결제 흐름이 남기는 데이터: 결제 DONE(토스), 포인트 USE 이력, 쿠폰 USED + coupon_usage */
    private Reservation paidReservation(String status) {
        Reservation r = fx.reservation(user, room, status, LocalDate.now().plusDays(5), LocalDate.now().plusDays(6));
        em.persist(Payment.builder()
                .reservation(r).userId(user.getUserId())
                .amount(BigDecimal.valueOf(90_000)).paymentMethod("카드")
                .paymentStatus(PaymentStatus.DONE).transactionId("pk_test_" + r.getReservationId())
                .pgProvider("TOSS").paymentDate(LocalDateTime.now())
                .build());
        em.persist(PointHistory.builder()
                .user(user).amount(-USED_POINT).type(PointHistoryType.USE)
                .reason("예약 시 포인트 사용").relatedId(r.getReservationId()).balance(BALANCE_AFTER_USE)
                .build());
        Coupon coupon = Coupon.builder()
                .name("5천원 할인").discountType("FIXED").discountValue(BigDecimal.valueOf(5000))
                .targetType("ALL").startDate(LocalDateTime.now().minusDays(1)).endDate(LocalDateTime.now().plusDays(30))
                .isActive(true)
                .build();
        em.persist(coupon);
        UserCoupon userCoupon = UserCoupon.builder()
                .user(user).coupon(coupon).status(UserCouponStatus.USED)
                .issuedAt(LocalDateTime.now().minusDays(1)).usedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(30))
                .build();
        em.persist(userCoupon);
        em.persist(CouponUsage.builder()
                .userCoupon(userCoupon).reservation(r).discountAmount(BigDecimal.valueOf(5000))
                .build());
        fx.flushAndClear();
        return r;
    }

    private void assertRefunded(Long reservationId) {
        em.flush();
        em.clear();
        Payment payment = em.createQuery("select p from Payment p where p.reservation.reservationId = :id", Payment.class)
                .setParameter("id", reservationId).getSingleResult();
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);

        User reloaded = em.find(User.class, user.getUserId());
        assertThat(reloaded.getPoint()).isEqualTo(BALANCE_AFTER_USE + USED_POINT);

        List<PointHistory> refunds = em.createQuery(
                        "select h from PointHistory h where h.relatedId = :id and h.type = :type", PointHistory.class)
                .setParameter("id", reservationId).setParameter("type", PointHistoryType.REFUND).getResultList();
        assertThat(refunds).hasSize(1);
        assertThat(refunds.get(0).getAmount()).isEqualTo(USED_POINT);
        assertThat(refunds.get(0).getBalance()).isEqualTo(BALANCE_AFTER_USE + USED_POINT);

        assertThat(em.createQuery("select uc.status from UserCoupon uc where uc.user.userId = :uid", String.class)
                .setParameter("uid", user.getUserId()).getSingleResult()).isEqualTo(UserCouponStatus.ISSUED);
        assertThat(em.createQuery("select count(cu) from CouponUsage cu where cu.reservation.reservationId = :id", Long.class)
                .setParameter("id", reservationId).getSingleResult()).isZero();

        assertThat(em.find(Reservation.class, reservationId).getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void 관리자_취소는_결제취소_포인트환불_쿠폰복원을_하고_두번째_호출은_아무것도_하지_않는다() {
        Reservation r = paidReservation(ReservationStatus.CONFIRMED);

        reservationService.cancelReservation(r.getReservationId(), "고객 요청");
        assertRefunded(r.getReservationId());
        verify(tossCancelClient, times(1)).cancel(eq("pk_test_" + r.getReservationId()), eq("고객 요청"));

        // 이미 취소된 예약: 서비스는 무시하고, 환불을 직접 다시 불러도 바꿀 것이 없다(멱등)
        reservationService.cancelReservation(r.getReservationId(), "중복");
        ReservationRefundService.RefundResult again =
                refundService.refund(em.find(Reservation.class, r.getReservationId()), "중복");
        assertThat(again.nothingChanged()).isTrue();
        assertRefunded(r.getReservationId());
        verify(tossCancelClient, times(1)).cancel(anyString(), anyString());
    }

    @Test
    void 취소요청_승인도_환불하고_갱신된_예약을_돌려준다() {
        Reservation r = paidReservation(ReservationStatus.CANCEL_REQUEST);

        var updated = reservationService.approveCancelRequest(r.getReservationId(), null);

        assertThat(updated.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertRefunded(r.getReservationId());
        verify(tossCancelClient).cancel(eq("pk_test_" + r.getReservationId()), anyString());
    }

    @Test
    void PG_취소가_실패하면_예외를_던지고_PG를_다시_부르지_않는다() {
        Reservation r = paidReservation(ReservationStatus.CONFIRMED);
        doThrow(new RefundFailedException("결제 취소(환불)에 실패했습니다: 테스트"))
                .when(tossCancelClient).cancel(anyString(), anyString());

        // 실제 요청에서는 이 예외로 트랜잭션 전체(예약 상태·포인트·쿠폰·결제 상태)가 롤백된다
        assertThatThrownBy(() -> reservationService.cancelReservation(r.getReservationId(), "고객 요청"))
                .isInstanceOf(RefundFailedException.class);
    }

    @Test
    void 결제가_없는_예약_취소는_PG를_호출하지_않는다() {
        Reservation r = fx.reservation(user, room, ReservationStatus.CONFIRMED,
                LocalDate.now().plusDays(8), LocalDate.now().plusDays(9));
        fx.flushAndClear();

        reservationService.cancelReservation(r.getReservationId(), "관리자 취소");

        verify(tossCancelClient, never()).cancel(anyString(), anyString());
        em.flush();
        em.clear();
        assertThat(em.find(Reservation.class, r.getReservationId()).getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(em.find(User.class, user.getUserId()).getPoint()).isEqualTo(BALANCE_AFTER_USE);
    }
}
