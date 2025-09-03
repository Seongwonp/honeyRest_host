package com.honeyrest.honeyrest_host.serviceAdmin;


import com.honeyrest.honeyrest_host.dto.DailyOverviewDTO;
import com.honeyrest.honeyrest_host.dto.GridCellDTO;
import com.honeyrest.honeyrest_host.dto.PriceCalendarDTO;
import com.honeyrest.honeyrest_host.dto.reports.SalesStatDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface PriceCalendarService {

    PriceCalendarDTO getMonth(Long companyId,
                              Long accommodationId,
                              YearMonth ym,
                              Integer minAvailable);

    boolean upsert(Long roomId, LocalDate date, BigDecimal price, Integer available);

    void bulkUpsert(List<BulkItem> items);

    void bulkUpsert(PriceCalendarDTO payload);

    List<SalesStatDTO> stats(Long accommodationId,
                             Long roomId,
                             LocalDate start, LocalDate end,
                             String granularity);

    Map<LocalDate, PriceCalendarDTO> getCalendarData(Long roomId, LocalDate startDate, LocalDate endDate);

    List<DailyOverviewDTO>  getDailyOverview(Long companyId, Long accommodationId, LocalDate start, LocalDate end);

    List<GridCellDTO> getGridCells(Long companyId, Long accommodationId, LocalDate start, LocalDate end);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class BulkItem {
        private Long roomId;
        private LocalDate date;
        private BigDecimal price;
        private Integer available;
    }
}