package com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection;

import java.math.BigDecimal;

public interface MonthlyAggRow {
    String getYm();                  // "2025-09"
    BigDecimal getTotal();
}
