package com.honeyrest.honeyrest_host.serviceCommon.refund;

import com.honeyrest.domain.entity.CouponUsage;
import com.honeyrest.domain.entity.Payment;
import com.honeyrest.domain.entity.PointHistory;
import com.honeyrest.domain.entity.Reservation;
import com.honeyrest.domain.entity.User;
import com.honeyrest.honeyrest_host.repositoryAdmin.CouponUsageRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.PaymentRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.PointHistoryRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import com.honeyrest.honeyrest_host.type.PaymentStatus;
import com.honeyrest.honeyrest_host.type.PointHistoryType;
import com.honeyrest.honeyrest_host.type.UserCouponStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 예약 취소에 따른 환불(결제 취소, 사용 포인트 환불, 쿠폰 복원).
 * 멱등하다: 두 번째 호출은 바꿀 것이 없다. 동시 취소는 호출자가 예약 행을 잠가 직렬화한다.
 * 예약 상태 변경과 같은 트랜잭션(MANDATORY)에서 호출하며, 토스 취소는 마지막에 해서 실패하면 전체가 롤백된다.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ReservationRefundService {

    private final PaymentRepository paymentRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final UserRepository userRepository;
    private final TossCancelClient tossCancelClient;

    /** 환불 결과 (모두 0 이면 이미 환불된 예약이거나 환불할 것이 없던 예약). */
    public record RefundResult(int canceledPayments, int refundedPoints, int restoredCoupons) {
        public boolean nothingChanged() {
            return canceledPayments == 0 && refundedPoints == 0 && restoredCoupons == 0;
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public RefundResult refund(Reservation reservation, String reason) {
        Long reservationId = reservation.getReservationId();
        String cancelReason = (reason == null || reason.isBlank()) ? "호스트 예약 취소" : reason;

        // 1) 결제: DONE → CANCELED. UPDATE 가 1건일 때만 이 호출이 취소 주체이므로 그때만 PG 취소 대상에 넣는다.
        List<String> paymentKeysToCancel = new ArrayList<>();
        int canceledPayments = 0;
        for (Payment payment : paymentRepository.findByReservationReservationIdOrderByPaymentIdAsc(reservationId)) {
            if (!PaymentStatus.DONE.equals(payment.getPaymentStatus())) continue;
            int updated = paymentRepository.changeStatusIfCurrent(
                    payment.getPaymentId(), PaymentStatus.DONE, PaymentStatus.CANCELED);
            if (updated == 0) continue;
            canceledPayments++;
            if (payment.getTransactionId() != null && !payment.getTransactionId().isBlank()) {
                paymentKeysToCancel.add(payment.getTransactionId()); // 토스는 paymentKey 를 transaction_id 에 저장
            } else {
                log.warn("[환불] 결제 키가 없어 PG 취소를 호출하지 못했습니다(수동 확인 필요): reservationId={}, paymentId={}",
                        reservationId, payment.getPaymentId());
            }
        }

        // 2) 포인트: 사용(USE, 음수 합) 중 아직 환불(REFUND, 양수 합)하지 않은 만큼
        int refundedPoints = refundPoints(reservation, cancelReason);

        // 3) 쿠폰: 사용 내역이 남아 있으면 쿠폰을 ISSUED 로 되돌리고 내역을 지운다
        int restoredCoupons = 0;
        List<CouponUsage> usages = couponUsageRepository.findByReservationId(reservationId);
        for (CouponUsage usage : usages) {
            restoredCoupons += couponUsageRepository.restoreUserCoupon(
                    usage.getUserCoupon().getUserCouponId(), UserCouponStatus.USED, UserCouponStatus.ISSUED);
        }
        if (!usages.isEmpty()) {
            couponUsageRepository.deleteAll(usages);
        }

        // 4) PG 취소는 마지막에: 실패하면 위 DB 변경까지 모두 롤백된다
        for (String paymentKey : paymentKeysToCancel) {
            tossCancelClient.cancel(paymentKey, cancelReason);
        }

        RefundResult result = new RefundResult(canceledPayments, refundedPoints, restoredCoupons);
        log.info("예약 환불 처리: reservationId={}, result={}", reservationId, result);
        return result;
    }

    private int refundPoints(Reservation reservation, String reason) {
        if (reservation.getUser() == null) return 0;
        Long userId = reservation.getUser().getUserId();
        Long reservationId = reservation.getReservationId();

        long used = -pointHistoryRepository.sumAmount(userId, reservationId, PointHistoryType.USE);
        long refunded = pointHistoryRepository.sumAmount(userId, reservationId, PointHistoryType.REFUND);
        long toRefund = used - refunded;
        if (toRefund <= 0) return 0;

        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new IllegalStateException("포인트를 환불할 회원이 없습니다. userId=" + userId));
        int balance = user.getPoint() + (int) toRefund;
        user.updatePoint(balance);
        pointHistoryRepository.save(PointHistory.builder()
                .user(user)
                .amount((int) toRefund)
                .type(PointHistoryType.REFUND)
                .reason("예약 취소 포인트 환불: " + reason)
                .relatedId(reservationId)
                .balance(balance)
                .build());
        return (int) toRefund;
    }
}
