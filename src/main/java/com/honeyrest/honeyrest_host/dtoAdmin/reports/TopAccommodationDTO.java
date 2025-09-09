package com.honeyrest.honeyrest_host.dtoAdmin.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopAccommodationDTO {
    private Long accommodationId;
    private String accommodationName;
    private BigDecimal totalSales;
    private BigDecimal totalOrdered;
}
