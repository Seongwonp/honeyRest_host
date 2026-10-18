package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.SalesChartPointDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ChartModeTest {

    @ParameterizedTest
    @CsvSource({
            "daily, DAILY", "일별, DAILY", "DAILY, DAILY",
            "weekly, WEEKLY", "주별, WEEKLY",
            "monthly, MONTHLY", "월별, MONTHLY", "' Monthly ', MONTHLY",
            "weekday, WEEKDAY", "요일별, WEEKDAY",
            "unknown, DAILY"
    })
    void parse_는_영문코드와_한글라벨을_받고_모르는_값은_DAILY(String input, ChartMode expected) {
        assertThat(ChartMode.parse(input)).isEqualTo(expected);
    }

    @Test
    void parse_null_은_DAILY() {
        assertThat(ChartMode.parse(null)).isEqualTo(ChartMode.DAILY);
    }

    @Test
    void 날짜_버킷은_드라이버별_타입을_모두_LocalDate_로_바꾼다() {
        LocalDate d = LocalDate.of(2026, 10, 5);
        assertThat(ChartMode.toLocalDate(Date.valueOf(d))).isEqualTo(d);
        assertThat(ChartMode.toLocalDate(Timestamp.valueOf(d.atStartOfDay()))).isEqualTo(d);
        assertThat(ChartMode.toLocalDate(d)).isEqualTo(d);
        assertThat(ChartMode.toLocalDate(LocalDateTime.of(2026, 10, 5, 13, 0))).isEqualTo(d);
        assertThat(ChartMode.toLocalDate("2026-10-05")).isEqualTo(d);
    }

    @Test
    void 행_변환은_단위별_라벨과_기본값을_채운다() {
        SalesChartPointDTO daily = ChartMode.DAILY.toPoint(new Object[]{Date.valueOf("2026-10-05"),
                new BigDecimal("1000"), 2L, new BigDecimal("500")});
        assertThat(daily.getLabel()).isEqualTo("2026-10-05");
        assertThat(daily.getTotalOrders()).isEqualTo(2);
        assertThat(daily.getAvgOrderPrice()).isEqualByComparingTo("500");

        SalesChartPointDTO monthly = ChartMode.MONTHLY.toPoint(new Object[]{"2026-10", new BigDecimal("3000")});
        assertThat(monthly.getLabel()).isEqualTo("2026-10");
        assertThat(monthly.getTotalOrders()).isZero();
        assertThat(monthly.getAvgOrderPrice()).isEqualByComparingTo("0");

        SalesChartPointDTO weekday = ChartMode.WEEKDAY.toPoint(new Object[]{7, null, 1});
        assertThat(weekday.getLabel()).isEqualTo("일");
        assertThat(weekday.getTotalSales()).isEqualByComparingTo("0");
    }
}
