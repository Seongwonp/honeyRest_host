package com.honeyrest.honeyrest_host.dto.accommodation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
public class AccommodationCreateRequestDTO {

    // 연관키 (필수)
    @NotNull
    private Long companyId;
    @NotNull
    private Long categoryId;
    @NotNull
    private Long mainRegionId;
    @NotNull
    private Long subRegionId;

    // 기본정보 (필수)
    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 500)
    private String address;

    // 위치
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private BigDecimal longitude;

    // 썸네일/설명
    @Size(max = 500)
    private String thumbnailUrl;

    private String description;

    // 편의시설(JSON) — 예: ["wifi","parking"]
    private JsonNode amenities;

    @Schema(type = "string", example = "15:00", description = "open (HH:mm)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime checkInTime;

    @Schema(type = "string", example = "11:00", description = "close (HH:mm)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime checkOutTime;

    // 상태 (미지정 시 서비스에서 "ACTIVE" 기본처리 권장)
    private String status;

    // 초기 부가정보 (선택)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal minPrice;

    // 이미지 일괄 등록
    private List<AccommodationImageDTO> images;

    // 태그 매핑 (tag_id 목록)
    private List<Long> tagIds;
}