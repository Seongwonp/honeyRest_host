package com.honeyrest.honeyrest_host.dto;

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
public class CalendarCellDTO {
    private LocalDate date;
    private BigDecimal price;   // null: 미설정 유지
    private Integer available;  // null: 미설정 유지
}
