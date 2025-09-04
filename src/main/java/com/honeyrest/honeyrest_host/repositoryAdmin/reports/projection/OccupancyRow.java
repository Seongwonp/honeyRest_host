package com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection;

import java.math.BigDecimal;

public interface OccupancyRow {
    // sold = 판매 객실박 수 (박 단위 합계)
    Integer getSoldNights();
    // available = 가용 객실박 수 (총객실수 * 날짜수)
    Integer getAvailableNights();
    BigDecimal getAdr();     // 평균 일일 요금
    BigDecimal getRevpar();  // Available당 매출
}
