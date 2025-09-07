package com.honeyrest.honeyrest_host.dtoAdmin.reports;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopRoomDTO {
    private Long accommodationId;
    private String accommodationName;
    private Long roomId;
    private String roomName;
    private BigDecimal totalSales;
    private Integer totalOrders;

}
