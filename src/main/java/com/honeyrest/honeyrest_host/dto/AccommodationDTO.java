package com.honeyrest.honeyrest_host.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// AccommodationDto.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationDTO {
    private Long AccommodationId;
    private Long companyId;
    private Long categoryId;
    private Long mainRegionId;
    private Long subRegionId;
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String thumbnailUrl; // 저장된 이미지 URL
    private JsonNode amenities; // JSON String
    private String description;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status; // ACTIVE / INACTIVE
}