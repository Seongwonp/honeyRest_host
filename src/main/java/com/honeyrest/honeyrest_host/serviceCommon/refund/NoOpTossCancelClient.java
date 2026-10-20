package com.honeyrest.honeyrest_host.serviceCommon.refund;

import lombok.extern.log4j.Log4j2;

/**
 * 토스 키가 없을 때 쓰는 구현. 외부 호출 없이 성공 처리하고 경고 로그를 남긴다(수동 환불 필요).
 */
@Log4j2
public class NoOpTossCancelClient implements TossCancelClient {

    @Override
    public void cancel(String paymentKey, String cancelReason) {
        log.warn("[PG 취소 생략] toss.secret-key 미설정 또는 screenshot 프로필 — 실제 결제는 취소되지 않았습니다. "
                + "운영 환경이면 수동 환불이 필요합니다: paymentKey={}", paymentKey);
    }
}
