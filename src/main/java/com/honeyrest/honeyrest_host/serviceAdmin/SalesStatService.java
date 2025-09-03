package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dto.reports.*;

import java.time.LocalDate;
import java.util.List;

public interface SalesStatService {
    public abstract List<SalesStatDTO> getDailySales(Long companyId, LocalDate from, LocalDate to, boolean zeroFill);
    List<TopAccommodationDTO> getTopAccommodations(Long companyId, LocalDate from, LocalDate to, int limit);
    List<UpcomingCheckinDTO> getUpcomingCheckins(Long companyId, LocalDate date, int size);
    CancelSummaryDTO getCancellationSummary(Long companyId, LocalDate from, LocalDate to);
    OccupancyDTO getOccupancy(Long companyId, LocalDate from, LocalDate to);


}
