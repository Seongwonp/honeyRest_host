package com.honeyrest.honeyrest_host.entity.enums;

public enum ReservationStatus {
    PENDING("대기중"),
    CONFIRMED("확정"),
    COMPLETED("완료"),
    CANCELLED("취소됨"),
    NO_SHOW("노쇼");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
//    	PENDING: 예약 요청 후 확정 대기
//		CONFIRMED: 결제 및 예약 확정
//		COMPLETED: 체크아웃까지 완료된 예약
//		CANCELLED: 예약 취소
//		NO_SHOW: 노쇼(예약했지만 안 옴)
