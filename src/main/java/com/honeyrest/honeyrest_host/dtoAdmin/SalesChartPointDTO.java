package com.honeyrest.honeyrest_host.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesChartPointDTO {

    // x축 라벨 (yyyy-MM-dd, yyyy-MM, 월~일 등 최종 문자열)
    private String label;

    // 지표
    private BigDecimal totalSales;      // 합계 매출
    private Integer    totalOrders;     // 주문 수
    private BigDecimal avgOrderPrice;   // 평균 주문 금액
}
