package com.honeyrest.honeyrest_host.serviceCommon;

/**
 * 요청 기간에 잔여 객실이 없어 예약 생성/상태 전환을 거절할 때 던진다 (사용자 API 의 409 와 같은 의미).
 */
public class ReservationConflictException extends IllegalStateException {

    public ReservationConflictException(String message) {
        super(message);
    }
}
