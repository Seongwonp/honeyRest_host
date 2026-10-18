package com.honeyrest.honeyrest_host.serviceCommon.refund;

/**
 * 토스페이먼츠 결제 취소 API 경계 (POST /v1/payments/{paymentKey}/cancel, 전액 취소).
 * <p>
 * 구현체는 {@link TossCancelClientConfig} 가 하나만 등록한다.
 * <ul>
 *     <li>{@link HttpTossCancelClient} — {@code toss.secret-key} 가 있고 screenshot 프로필이 아닐 때. 실제 토스 API 호출.</li>
 *     <li>{@link NoOpTossCancelClient} — 키가 비어 있거나 screenshot 프로필. 외부 호출 없이 경고 로그만 남긴다.</li>
 * </ul>
 * 호출 규약: 취소 성공(이미 취소된 결제 포함)이면 정상 반환, 그 외에는 {@link RefundFailedException}.
 */
public interface TossCancelClient {

    void cancel(String paymentKey, String cancelReason);
}
