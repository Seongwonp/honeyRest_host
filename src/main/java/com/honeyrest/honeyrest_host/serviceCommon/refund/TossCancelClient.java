package com.honeyrest.honeyrest_host.serviceCommon.refund;

/**
 * 토스페이먼츠 결제 전액 취소 (POST /v1/payments/{paymentKey}/cancel). 구현체는 {@link TossCancelClientConfig} 가 고른다.
 * 이미 취소된 결제도 성공으로 반환하고, 실패하면 {@link RefundFailedException}.
 */
public interface TossCancelClient {

    void cancel(String paymentKey, String cancelReason);
}
