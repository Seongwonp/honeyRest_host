package com.honeyrest.honeyrest_host.dto.reports;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OccupancyDTO {
    private Double occupancyRate;      // 점유율(0~1)
    private BigDecimal adr;            // Average Daily Rate
    private BigDecimal revpar;         // Revenue per Available Room
    private Integer soldNights;        // 판매 객실박수
    private Integer availableNights;   // 가용 객실박수

    public static OccupancyDTO empty() {
        return OccupancyDTO.builder()
                .occupancyRate(0.0).adr(BigDecimal.ZERO).revpar(BigDecimal.ZERO)
                .soldNights(0).availableNights(0).build();
    }
}
