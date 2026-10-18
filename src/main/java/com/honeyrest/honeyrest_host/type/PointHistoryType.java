package com.honeyrest.honeyrest_host.type;

/**
 * 포인트 이력 유형 (point_history.type).
 * 사용자 API PointService.TYPE_* 와 같은 문자열이어야 한다 (예약 시 사용 = USE, 취소 환불 = REFUND).
 */
public final class PointHistoryType {

    public static final String SAVE = "SAVE";
    public static final String USE = "USE";
    public static final String REFUND = "REFUND";

    private PointHistoryType() {
    }
}
