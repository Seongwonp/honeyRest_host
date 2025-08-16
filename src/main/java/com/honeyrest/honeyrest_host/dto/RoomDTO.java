package com.honeyrest.honeyrest_host.dto;

import com.honeyrest.honeyrest_host.entity.enums.OperationStatus;
import com.honeyrest.honeyrest_host.entity.enums.RoomType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// RoomDto.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Long roomId;

    @NotNull(message = "업체 ID(accommodationId)는 필수입니다.")
    private Long accommodationId;

    // @NotBlank(message = "객실 이름은 필수입니다.")  // ← 선택
    private String name;

    private RoomType type; // Standard, Deluxe...
    private BigDecimal price;            // @NotNull 붙여도 OK (선택)
    private Integer maxOccupancy;        // @NotNull 붙여도 OK (선택)
    private Integer standardOccupancy;
    private BigDecimal extraPersonFee;
    private String bedInfo; // JSON String
    private String amenities; // JSON String
    private String description;
    private Integer totalRooms;
    private OperationStatus status; // ACTIVE / INACTIVE
}