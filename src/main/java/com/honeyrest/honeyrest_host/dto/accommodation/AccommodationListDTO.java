package com.honeyrest.honeyrest_host.dto.accommodation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccommodationListDTO {
    private Long accommodationId;
    private String name;
    private String categoryName;
    private String regionName;     // mainRegion.name
    private BigDecimal minPrice;
    private String status;
}