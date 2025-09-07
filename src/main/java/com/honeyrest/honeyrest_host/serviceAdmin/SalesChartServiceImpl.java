package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.SalesChartPointDTO;
import com.honeyrest.honeyrest_host.repositoryAdmin.SalesStatRepository;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesChartServiceImpl implements SalesChartService {
    private final SalesStatRepository repo;

    // 공통 변환기
    private List<SalesChartPointDTO> toChartDTO(List<Object[]> rows, String mode) {
        DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<SalesChartPointDTO> out = new ArrayList<>();

        for (Object[] r : rows) {
            String label;
            BigDecimal totalSales;
            Integer totalOrders;
            BigDecimal avgOrderPrice;

            switch (mode) {
                case "daily" -> {
                    LocalDate date;
                    if (r[0] instanceof java.sql.Date sqlDate) {
                        date = sqlDate.toLocalDate();
                    } else if (r[0] instanceof java.sql.Timestamp ts) {
                        date = ts.toLocalDateTime().toLocalDate();
                    } else if (r[0] instanceof LocalDate ld) {
                        date = ld;
                    } else {
                        throw new IllegalArgumentException("지원하지 않는 날짜 타입: " + r[0].getClass());
                    }

                    label = d.format(date);
                    totalSales    = (BigDecimal) r[1];
                    totalOrders   = ((Number) r[2]).intValue();
                    avgOrderPrice = (BigDecimal) r[3];
                }
                case "weekly" -> {
                    LocalDate mon;
                    if (r[0] instanceof java.sql.Date sqlDate) {
                        mon = sqlDate.toLocalDate();
                    } else if (r[0] instanceof java.sql.Timestamp ts) {
                        mon = ts.toLocalDateTime().toLocalDate();
                    } else if (r[0] instanceof LocalDate ld) {
                        mon = ld;
                    } else {
                        throw new IllegalArgumentException("지원하지 않는 날짜 타입: " + r[0].getClass());
                    }

                    label = d.format(mon);
                    totalSales    = (BigDecimal) r[1];
                    totalOrders   = ((Number) r[2]).intValue();
                    avgOrderPrice = BigDecimal.ZERO;
                }
                case "monthly" -> {
                    label          = String.valueOf(r[0]); // "yyyy-MM"
                    totalSales     = (BigDecimal) r[1];
                    totalOrders    = null;
                    avgOrderPrice  = null;
                }
                case "weekday" -> {
                    int dow        = ((Number) r[0]).intValue(); // 1~7 (월~일)
                    label          = "월화수목금토일".substring(dow-1, dow);
                    totalSales     = (BigDecimal) r[1];
                    totalOrders    = ((Number) r[2]).intValue();
                    avgOrderPrice  = BigDecimal.ZERO;
                }
                default -> throw new IllegalArgumentException("unknown mode");
            }

            out.add(SalesChartPointDTO.builder()
                    .label(label)
                    .totalSales(totalSales == null ? BigDecimal.ZERO : totalSales)
                    .totalOrders(totalOrders == null ? 0 : totalOrders)
                    .avgOrderPrice(avgOrderPrice == null ? BigDecimal.ZERO : avgOrderPrice)
                    .build());
        }
        return out;
    }

    @Override
    public List<SalesChartPointDTO> getChart(String mode,
                                             List<Long> accIds,
                                             LocalDate from,
                                             LocalDate to) {

        mode = switch (mode) {
            case "일별", "daily"   -> "daily";
            case "주별", "weekly"  -> "weekly";
            case "월별", "monthly" -> "monthly";
            case "요일별","weekday"-> "weekday";
            default -> "daily";
        };

        List<Object[]> raw;
        return switch (mode) {
            case "daily"   -> { raw = repo.findDaily(accIds, from, to);   yield toChartDTO(raw, mode); }
            case "weekly"  -> { raw = repo.findWeekly(accIds, from, to);  yield toChartDTO(raw, mode); }
            case "monthly" -> { raw = repo.findMonthly(accIds, from, to); yield toChartDTO(raw, mode); }
            case "weekday" -> { raw = repo.findWeekday(accIds, from, to); yield toChartDTO(raw, mode); }
            default -> List.of();
        };
    }
}
