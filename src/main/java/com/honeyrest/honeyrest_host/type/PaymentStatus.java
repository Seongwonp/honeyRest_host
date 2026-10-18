package com.honeyrest.honeyrest_host.type;

/**
 * 결제 상태 문자열 상수 (payment.payment_status VARCHAR(20)).
 * <p>
 * 사용자 API 는 토스 승인 응답의 status 를 그대로 저장한다(PaymentService: {@code paymentStatus(result.getStatus())}).
 * 따라서 철자도 토스 규격을 따른다: 승인 완료 {@code DONE}, 전액 취소 {@code CANCELED}(L 하나).
 * 예약 상태의 CANCELLED(L 둘)와 철자가 다르므로 섞어 쓰지 않는다.
 */
public final class PaymentStatus {

    /** 결제 승인 완료 (매출 집계 대상) */
    public static final String DONE = "DONE";
    /** 전액 취소(환불) 완료 */
    public static final String CANCELED = "CANCELED";

    private PaymentStatus() {
    }
}
