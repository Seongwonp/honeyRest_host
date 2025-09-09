package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.reports.CancelSummaryDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.OccupancyDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.UpcomingCheckinDTO;

import java.time.LocalDate;
import java.util.List;

public interface DashboardReportService {
    OccupancyDTO getOccupancy(List<Long> accIds, LocalDate from, LocalDate to);

    CancelSummaryDTO getCancelSummary(List<Long> accIds, LocalDate from, LocalDate to);

    List<UpcomingCheckinDTO> getTodayCheckins(List<Long> accIds, LocalDate today);
}
