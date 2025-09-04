package com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface SalesStatRow {
    LocalDate getBucket();                 // yyyy-MM-dd / yyyy-MM (월) / Monday 등
    BigDecimal getTotalSales();
    Integer getTotalOrders();
    // 선택 필드
    Integer getDayOfWeek();             // 1=Mon..7=Sun (요일 뷰에서만 셋)
    BigDecimal getAvgOrderPrice();// 이전기간 대비 증감액 (서비스에서 계산하는 걸 권장)
    BigDecimal getSalesDiff();
    Double getSalesGrowthRate();
    Long accommodationId();

}
