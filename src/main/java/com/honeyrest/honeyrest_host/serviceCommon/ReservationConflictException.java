package com.honeyrest.honeyrest_host.serviceCommon;

/**
 * 요청 기간에 해당 객실의 잔여 재고가 없어 예약 생성/상태 전환을 거절할 때 던진다.
 * 사용자 API 의 409 응답과 같은 의미이며, 화면 컨트롤러는 flash 메시지(error)로 보여준다.
 */
public class ReservationConflictException extends IllegalStateException {

    public ReservationConflictException(String message) {
        super(message);
    }
}
