package com.honeyrest.honeyrest_host.serviceCommon.refund;

/**
 * PG(토스) 결제 취소에 실패했을 때 던진다. 호출 트랜잭션이 롤백되어 예약 상태·포인트·쿠폰도 원래대로 남는다.
 * IllegalStateException 하위라 화면 컨트롤러의 기존 상태 충돌 처리(flash error / 409)로 사유가 보인다.
 */
public class RefundFailedException extends IllegalStateException {

    public RefundFailedException(String message) {
        super(message);
    }

    public RefundFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
