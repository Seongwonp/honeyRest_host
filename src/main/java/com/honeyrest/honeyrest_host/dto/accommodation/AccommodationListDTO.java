package com.honeyrest.honeyrest_host.dto.accommodation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationListDTO {
    private Long accommodationId;
    private String name;
    private String categoryName;
    private String regionName;     // mainRegion.name
    private BigDecimal minPrice;
    private String status;

    private String thumbnailUrl;
}