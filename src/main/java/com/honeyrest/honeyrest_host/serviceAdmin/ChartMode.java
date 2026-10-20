package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.SalesChartPointDTO;
import com.honeyrest.honeyrest_host.repositoryAdmin.SalesStatRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 매출 차트 집계 단위별 조회와 행 변환. 행 형식은 {@link SalesStatRepository} 참고.
 */
public enum ChartMode {

    /** 일별: [날짜, 매출, 주문 수, 평균 주문가] */
    DAILY("daily", "일별") {
        @Override
        List<Object[]> fetch(SalesStatRepository repo, List<Long> accIds, LocalDate from, LocalDate to) {
            return repo.findDaily(accIds, from, to);
        }

        @Override
        SalesChartPointDTO toPoint(Object[] r) {
            return point(DATE_LABEL.format(toLocalDate(r[0])), r[1], r[2], r[3]);
        }
    },
    /** 주별(대표일 = 월요일): [월요일 날짜, 매출, 주문 수, NULL] */
    WEEKLY("weekly", "주별") {
        @Override
        List<Object[]> fetch(SalesStatRepository repo, List<Long> accIds, LocalDate from, LocalDate to) {
            return repo.findWeekly(accIds, from, to);
        }

        @Override
        SalesChartPointDTO toPoint(Object[] r) {
            return point(DATE_LABEL.format(toLocalDate(r[0])), r[1], r[2], null);
        }
    },
    /** 월별: ["yyyy-MM", 매출] */
    MONTHLY("monthly", "월별") {
        @Override
        List<Object[]> fetch(SalesStatRepository repo, List<Long> accIds, LocalDate from, LocalDate to) {
            return repo.findMonthly(accIds, from, to);
        }

        @Override
        SalesChartPointDTO toPoint(Object[] r) {
            return point(String.valueOf(r[0]), r[1], null, null);
        }
    },
    /** 요일별(월=1 … 일=7): [요일 번호, 매출, 주문 수, NULL] */
    WEEKDAY("weekday", "요일별") {
        @Override
        List<Object[]> fetch(SalesStatRepository repo, List<Long> accIds, LocalDate from, LocalDate to) {
            return repo.findWeekday(accIds, from, to);
        }

        @Override
        SalesChartPointDTO toPoint(Object[] r) {
            int dow = ((Number) r[0]).intValue();
            return point(WEEKDAY_LABELS.substring(dow - 1, dow), r[1], r[2], null);
        }
    };

    private static final DateTimeFormatter DATE_LABEL = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String WEEKDAY_LABELS = "월화수목금토일";

    private final String code;
    private final String koreanLabel;

    ChartMode(String code, String koreanLabel) {
        this.code = code;
        this.koreanLabel = koreanLabel;
    }

    public String code() {
        return code;
    }

    /**
     * 화면/요청의 모드 문자열("daily", "일별" 등, 대소문자·공백 무시)을 해석한다.
     * 모르는 값·null 이면 {@link #DAILY}.
     */
    public static ChartMode parse(String mode) {
        if (mode == null) return DAILY;
        String m = mode.trim();
        for (ChartMode c : values()) {
            if (c.code.equalsIgnoreCase(m) || c.koreanLabel.equals(m) || c.name().equalsIgnoreCase(m)) {
                return c;
            }
        }
        return DAILY;
    }

    abstract List<Object[]> fetch(SalesStatRepository repo, List<Long> accIds, LocalDate from, LocalDate to);

    abstract SalesChartPointDTO toPoint(Object[] row);

    /** JDBC 드라이버/DB 에 따라 날짜 버킷이 java.sql.Date, Timestamp, LocalDate, 문자열로 올 수 있다. */
    static LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate ld) return ld;
        if (value instanceof java.sql.Date sqlDate) return sqlDate.toLocalDate();
        if (value instanceof java.sql.Timestamp ts) return ts.toLocalDateTime().toLocalDate();
        if (value instanceof java.time.LocalDateTime ldt) return ldt.toLocalDate();
        if (value instanceof CharSequence cs) return LocalDate.parse(cs.toString().substring(0, 10));
        throw new IllegalArgumentException("지원하지 않는 날짜 타입: " + (value == null ? "null" : value.getClass()));
    }

    private static SalesChartPointDTO point(String label, Object sales, Object orders, Object avg) {
        return SalesChartPointDTO.builder()
                .label(label)
                .totalSales(toDecimal(sales))
                .totalOrders(orders == null ? 0 : ((Number) orders).intValue())
                .avgOrderPrice(toDecimal(avg))
                .build();
    }

    private static BigDecimal toDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        return new BigDecimal(value.toString());
    }
}
