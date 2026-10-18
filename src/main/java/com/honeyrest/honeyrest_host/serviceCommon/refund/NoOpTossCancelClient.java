package com.honeyrest.honeyrest_host.serviceCommon.refund;

import lombok.extern.log4j.Log4j2;

/**
 * 토스 시크릿 키가 없거나(로컬·test) screenshot 프로필일 때 쓰는 구현.
 * 외부 호출 없이 성공으로 처리하고, 운영에서 키 누락을 알아챌 수 있게 경고 로그를 남긴다(수동 환불 필요).
 */
@Log4j2
public class NoOpTossCancelClient implements TossCancelClient {

    @Override
    public void cancel(String paymentKey, String cancelReason) {
        log.warn("[PG 취소 생략] toss.secret-key 미설정 또는 screenshot 프로필 — 실제 결제는 취소되지 않았습니다. "
                + "운영 환경이면 수동 환불이 필요합니다: paymentKey={}", paymentKey);
    }
}
