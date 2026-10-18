package com.honeyrest.honeyrest_host.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarCellDTO {
    private LocalDate date;
    /**
     * 설정된 1박 가격: price_calendar 의 그날 가격, 없으면 객실 기본가(room.price).
     * 사용자 API 가 그날 판매가로 쓰는 값이며 수정 모달의 기본값이다. 벌크 업서트 JSON 에서는 바꿀 가격(null: 유지).
     * (과거에는 이 필드에 그날 체크인 매출 합계를 넣어 수정 모달이 매출을 가격으로 미리 채웠다.)
     */
    private BigDecimal price;
    /** 그날 체크인한 점유 예약 금액 합계(표시 전용). 체크인이 없으면 null. */
    private BigDecimal revenue;
    /** 잔여 객실 = 총 객실 − 그날 겹치는 점유 예약 수 (표시 전용, 조회 시 계산). 벌크 JSON 에서는 null: 유지 */
    private Integer available;
}
