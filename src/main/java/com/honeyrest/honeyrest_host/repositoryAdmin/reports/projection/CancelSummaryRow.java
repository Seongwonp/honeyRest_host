package com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection;

public interface CancelSummaryRow {
    Integer getTotal();      // 전체 예약 수
    Integer getCanceled();   // 취소 예약 수
}
