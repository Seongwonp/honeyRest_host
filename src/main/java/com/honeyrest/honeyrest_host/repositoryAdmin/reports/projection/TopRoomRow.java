package com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection;

import java.math.BigDecimal;

public interface TopRoomRow {
    Long getRoomId();
    String getRoomName();
    Long getAccommodationId();
    String getAccommodationName();
    Integer getTotalOrders();
    BigDecimal getTotalSales();
}
