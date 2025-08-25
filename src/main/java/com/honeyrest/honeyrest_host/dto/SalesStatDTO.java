package com.honeyrest.honeyrest_host.dto;

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
}
