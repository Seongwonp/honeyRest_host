package com.honeyrest.honeyrest_host.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomHeaderDTO {
    //    객실 메타데이터 표현용.
    private Long roomId;
    private String roomName;
    private Long accommodationId;
    private String accommodationName;
    private Integer totalRooms;     // room.totalRooms
    private BigDecimal basePrice;   // room.price (기본가)

}