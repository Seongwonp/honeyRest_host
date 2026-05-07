package com.honeyrest.honeyrest_host.serviceAdmin;


import com.honeyrest.honeyrest_host.dtoAdmin.reports.CancelSummaryDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.OccupancyDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.UpcomingCheckinDTO;
import com.honeyrest.honeyrest_host.repositoryAdmin.DashboardReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class DashboardReportServiceImpl implements DashboardReportService {
    private final DashboardReportRepository repo;

    @Override
    public OccupancyDTO getOccupancy(List<Long> accIds, LocalDate from, LocalDate to) {
        // 날짜 방어: from > to 인 경우 스왑
        if (from == null || to == null) {
            throw new IllegalArgumentException("from/to must not be null");
        }
        if (from.isAfter(to)) {
            LocalDate tmp = from; from = to; to = tmp;
        }

        // Repo 값 널/타입 방어
        Integer soldRaw = repo.sumSoldNights(accIds, from, to);
        BigDecimal revenueRaw = repo.sumRevenue(accIds, from, to);
        Integer roomsRaw = repo.countActiveRooms(accIds);

        int sold = soldRaw == null ? 0 : soldRaw;
        BigDecimal revenue = revenueRaw == null ? BigDecimal.ZERO : revenueRaw;
        int rooms = roomsRaw == null ? 0 : roomsRaw;

        // 기간 일수 (양수 보장)
        int days = (int) (to.plusDays(1).toEpochDay() - from.toEpochDay());
        if (days < 0) days = 0;

        // 가용박수 = 가용객실수 * 일수
        int available = Math.max(rooms * days, 0);

        // 점유율
        double occupancy = (available == 0) ? 0.0 : (sold / (double) available);

        // ADR, RevPAR (0 으로 나눔 방어 + 반올림 방식 고정)
        BigDecimal adr = (sold == 0)
                ? BigDecimal.ZERO
                : revenue.divide(BigDecimal.valueOf(sold), 0, HALF_UP);

        BigDecimal revpar = (available == 0)
                ? BigDecimal.ZERO
                : revenue.divide(BigDecimal.valueOf(available), 0, HALF_UP);

        return OccupancyDTO.builder()
                .occupancyRate(occupancy)          // 0.0 ~ 1.0 (뷰에서 % 변환)
                .adr(adr)
                .revpar(revpar)
                .soldNights(sold)
                .availableNights(available)
                .build();
    }

    @Override
    public CancelSummaryDTO getCancelSummary(List<Long> accIds, LocalDate from, LocalDate to) {
        List<Object[]> rows = repo.cancelSummary(accIds, from, to);
        Object[] row = (rows != null && !rows.isEmpty()) ? rows.get(0) : new Object[]{0, 0};

        int total    = ((Number) row[0]).intValue(); // BigInteger/Long도 안전하게 처리됨
        int canceled = ((Number) row[1]).intValue();
        double rate  = total == 0 ? 0.0 : (canceled / (double) total);

        return CancelSummaryDTO.builder()
                .total(total)
                .canceled(canceled)
                .cancelRate(rate)
                .build();
    }

    @Override
        public List<UpcomingCheckinDTO> getTodayCheckins(List<Long> accIds, LocalDate today) {
            if (today == null) today = LocalDate.now();

            List<Object[]> rows = repo.findTodayCheckins(accIds, today);
            List<UpcomingCheckinDTO> out = new ArrayList<>();
            if (rows == null || rows.isEmpty()) return out;

            for (Object[] r : rows) {
                if (r == null) continue;

                Long reservationId = (r.length > 0 && r[0] instanceof Number n0) ? n0.longValue() : null;
                String guestName = (r.length > 1 && r[1] != null) ? r[1].toString() : "-";
                String accommodationName = (r.length > 2 && r[2] != null) ? r[2].toString() : "";
                String roomName = (r.length > 3 && r[3] != null) ? r[3].toString() : "";
                LocalDate checkIn = null;
                if (r.length > 4 && r[4] != null) {
                    if (r[4] instanceof java.sql.Date d) checkIn = d.toLocalDate();
                    else if (r[4] instanceof LocalDate ld) checkIn = ld;
                }
                int nights = (r.length > 5 && r[5] instanceof Number n5) ? n5.intValue() : 0;

                out.add(UpcomingCheckinDTO.builder()
                        .reservationId(reservationId)
                        .guestName(guestName)
                        .accommodationName(accommodationName)
                        .roomName(roomName)
                        .checkIn(checkIn)
                        .nights(nights)
                        .build());
            }
            return out;
        }
    }
