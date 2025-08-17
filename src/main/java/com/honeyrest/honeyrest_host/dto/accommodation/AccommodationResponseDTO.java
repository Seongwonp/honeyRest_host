package com.honeyrest.honeyrest_host.dto.accommodation;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(type = "string", example = "15:00", description = "open (HH:mm)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime checkInTime;

    @Schema(type = "string", example = "11:00", description = "close (HH:mm)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime checkOutTime;

    // 상태/지표
    private String status;
    private BigDecimal rating;
    private BigDecimal minPrice;

    // 부가
    private List<AccommodationImageDTO> images;
    private List<AccommodationTagMapDTO> tags;
}
