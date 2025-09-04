package com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection;

import java.math.BigDecimal;

public interface DailySalesRow {
    String getBucket();        // yyyy-MM-dd
    BigDecimal getTotalSales();
    Integer getTotalOrders();
    BigDecimal getAvgOrderPrice();
    Long getAccommodationId();
    String getAccommodationName();
}
