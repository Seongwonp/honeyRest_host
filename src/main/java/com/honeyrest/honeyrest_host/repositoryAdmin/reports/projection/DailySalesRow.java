package com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

public interface DailySalesRow {
    LocalDate getBucket();        // yyyy-MM-dd
    BigDecimal getTotalSales();
    Integer getTotalOrders();
    BigDecimal getAvgOrderPrice();
    Long getAccommodationId();
    String getAccommodationName();
}
