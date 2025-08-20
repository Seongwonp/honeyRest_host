package com.honeyrest.honeyrest_host.dtoOwner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceCalendarDTO {
    private Long calendarId;
    private Long roomId;
    private LocalDate date;
    private BigDecimal price;
    private int availableRoom;
}
