package com.honeyrest.honeyrest_host.dtoAdmin;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GridCellDTO {

    private Long roomId;
    private String roomName;
    private LocalDate date;
    private BigDecimal price;
    private Integer available;
    private Integer totalRooms;
}
