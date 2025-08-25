package com.honeyrest.honeyrest_host.service;


import com.honeyrest.honeyrest_host.dto.PriceInventoryCalendarDTO;
import com.honeyrest.honeyrest_host.dto.SalesStatDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface PriceCalendarService {

    PriceInventoryCalendarDTO getMonth(Long companyId,
                                       Long accommodationId,
                                       YearMonth ym,
                                       Integer minAvailable);

    boolean upsert(Long roomId, LocalDate date, BigDecimal price, Integer available);

    void bulkUpsert(List<BulkItem> items);

    void bulkUpsert(PriceInventoryCalendarDTO payload);

    List<SalesStatDTO> stats(Long accommodationId,
                             Long roomId,
                             LocalDate start, LocalDate end,
                             String granularity);

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