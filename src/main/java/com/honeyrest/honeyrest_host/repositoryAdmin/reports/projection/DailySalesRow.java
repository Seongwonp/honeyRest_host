package com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailySalesRow {
    LocalDate getBucket();        // yyyy-MM-dd
    BigDecimal getTotalSales();
    Integer getTotalOrders();
    BigDecimal getAvgOrderPrice();
    Long getAccommodationId();
    String getAccommodationName();
}
