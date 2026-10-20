package com.honeyrest.honeyrest_host.type;

/**
 * 결제 상태 문자열 상수 (payment.payment_status). 토스 규격 철자를 따른다.
 * 결제 취소 CANCELED(L 하나)는 예약 상태 CANCELLED(L 둘)와 다르다.
 */
public final class PaymentStatus {

    /** 결제 승인 완료 (매출 집계 대상) */
    public static final String DONE = "DONE";
    /** 전액 취소(환불) 완료 */
    public static final String CANCELED = "CANCELED";

    private PaymentStatus() {
    }
}
