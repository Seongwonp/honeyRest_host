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
     * 1박 가격: price_calendar 의 그날 가격, 없으면 객실 기본가. 벌크 업서트 JSON 에서는 null 이면 유지.
     */
    private BigDecimal price;
    /** 그날 체크인한 점유 예약 금액 합계(표시 전용). 체크인이 없으면 null. */
    private BigDecimal revenue;
    /** 잔여 객실 = 총 객실 − 그날 겹치는 점유 예약 수 (표시 전용, 조회 시 계산). 벌크 JSON 에서는 null: 유지 */
    private Integer available;
}
