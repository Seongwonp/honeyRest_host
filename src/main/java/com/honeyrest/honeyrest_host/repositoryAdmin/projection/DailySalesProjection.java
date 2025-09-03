package com.honeyrest.honeyrest_host.repositoryAdmin.projection;

public interface DailySalesProjection {
    java.sql.Date getBucket();      // DATE(p.payment_date) 별칭과 동일
    java.math.BigDecimal getTotalSales();
    Integer getTotalOrders();
    java.math.BigDecimal getAvgOrderPrice();
    Long getAccommodationId();      // 선택: 숙소별 시리즈가 필요할 때
    String getAccommodationName();  // 선택
}
