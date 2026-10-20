package com.honeyrest.honeyrest_host.serviceCommon.refund;

/**
 * PG(토스) 결제 취소 실패. 호출 트랜잭션 전체가 롤백된다.
 * IllegalStateException 하위라 컨트롤러의 상태 충돌 처리로 사유가 보인다.
 */
public class RefundFailedException extends IllegalStateException {

    public RefundFailedException(String message) {
        super(message);
    }

    public RefundFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
