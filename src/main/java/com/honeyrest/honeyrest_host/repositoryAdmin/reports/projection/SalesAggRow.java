package com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection;


import org.joda.time.LocalDate;

import java.math.BigDecimal;

public interface SalesAggRow {
    LocalDate getBucket();
    BigDecimal getTotalSales();
    Integer getTotalOrders();
    BigDecimal getAvgOrderPrice();
    Integer getDayOfWeek();      // 요일 통계에서만 값 존재
    Long getAccommodationId();   // 그룹에 포함
}
