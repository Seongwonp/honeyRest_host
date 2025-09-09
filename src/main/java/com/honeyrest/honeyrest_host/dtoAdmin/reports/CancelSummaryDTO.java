package com.honeyrest.honeyrest_host.dtoAdmin.reports;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelSummaryDTO {
    private Integer total;       // 전체 예약
    private Integer canceled;    // 취소 예약
    private Double cancelRate;   // 취소율(0~1)

    public static CancelSummaryDTO empty() {
        return CancelSummaryDTO.builder().total(0).canceled(0).cancelRate(0.0).build();
    }
}
