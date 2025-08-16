package com.honeyrest.honeyrest_host.dto.accommodation;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationResponseDTO {

    private Long accommodationId;

    private Long companyId;
    private Long categoryId;
    private Long mainRegionId;
    private Long subRegionId;

    // 기본정보
    private String name;
    private String address;

    // 위치/미디어
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String thumbnailUrl;

    // 설명/편의시설
    private String description;
    private JsonNode amenities;

    // 운영시간
    private LocalTime checkInTime;
    private LocalTime checkOutTime;

    // 상태/지표
    private String status;
    private BigDecimal rating;
    private BigDecimal minPrice;

    // 부가
    private List<AccommodationImageDTO> images;
    private List<AccommodationTagMapDTO> tags;
}
