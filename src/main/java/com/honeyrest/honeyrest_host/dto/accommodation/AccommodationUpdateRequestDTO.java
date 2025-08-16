package com.honeyrest.honeyrest_host.dto.accommodation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.honeyrest.honeyrest_host.entity.enums.OperationStatus;
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
public class AccommodationUpdateRequestDTO {
    // 수정은 부분 업데이트 허용 가정 → 전부 nullable
    private Long companyId;
    private Long categoryId;
    private Long mainRegionId;
    private Long subRegionId;

    private String name;
    private String address;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private String thumbnailUrl;
    private String description;

    private JsonNode amenities;         // ["wifi", ...]
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime checkInTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime checkOutTime;

    private OperationStatus status;              // ACTIVE / INACTIVE
    private BigDecimal minPrice;

    // 선택: 이미지/태그 전체 교체(덮어쓰기)를 할지, 부분 수정할지는 정책에 따라
    private List<AccommodationImageDTO> images; // 덮어쓰기 정책(예시)
    private List<Long> tagIds;                          // 덮어쓰기 정책(예시)
}

