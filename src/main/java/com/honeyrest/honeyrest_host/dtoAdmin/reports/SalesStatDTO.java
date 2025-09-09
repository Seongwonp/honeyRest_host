package com.honeyrest.honeyrest_host.dtoAdmin.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesStatDTO {
    private LocalDate bucket;        // 집계 기준일 (일/주/월)
    private BigDecimal totalSales;   // 매출 합계
    private Integer totalOrders;     // 주문 수
    private BigDecimal avgOrderPrice;// 선택
    private Integer dayOfWeek; // 1=월요일 ... 7=일요일
    private BigDecimal salesDiff;    // 이전 기간 대비 매출 증감액
    private Double salesGrowthRate;  // 증가율 (%)
    private Long accommodationId;  // 숙소 단위 집계일 경우


}
