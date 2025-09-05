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
public class DailySalesDTO {

    private LocalDate date;         // 일자 (예: 2025-09-04) x축
    private BigDecimal totalSales;  // 해당 날짜의 총 매출 y축
    private Integer totalOrders;    // 해당 날짜의 총 주문 건수
    private BigDecimal avgOrderPrice; // 평균 주문 금액 (선택)

    private Long accommodationId;   // 선택: 숙소별 구분이 필요하다면
    private String accommodationName; // 선택: 그래프 범례에 표시할 이름
}
