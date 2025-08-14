package com.honeyrest.honeyrest_host.dto;

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
    private Long accommodationId;
    private String name;
    private String type; // Standard, Deluxe...
    private BigDecimal price;
    private Integer maxOccupancy;
    private Integer standardOccupancy;
    private BigDecimal extraPersonFee;
    private String bedInfo; // JSON String
    private String amenities; // JSON String
    private String description;
    private Integer totalRooms;
    private String status; // ACTIVE / INACTIVE
}
