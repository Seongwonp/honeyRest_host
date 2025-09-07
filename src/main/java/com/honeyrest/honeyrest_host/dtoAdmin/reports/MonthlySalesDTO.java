package com.honeyrest.honeyrest_host.dtoAdmin.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlySalesDTO {
    private YearMonth ym;
    private BigDecimal total; // 월 합계
}
