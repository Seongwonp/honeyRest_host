package com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection;

import java.math.BigDecimal;

public interface TopAccommodationRow {
    Long getAccommodationId();
    String getAccommodationName();
    BigDecimal getTotalSales();
}
