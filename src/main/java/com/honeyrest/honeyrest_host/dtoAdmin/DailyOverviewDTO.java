package com.honeyrest.honeyrest_host.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyOverviewDTO {
    private LocalDate date;
    private Integer totalRoomsSum;
    private Integer availableSum;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
