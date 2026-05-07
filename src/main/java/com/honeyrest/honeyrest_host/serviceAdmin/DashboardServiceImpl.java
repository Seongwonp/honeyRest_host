package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.DashboardDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.SalesChartPointDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.DailySalesDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.MonthlySalesDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.TopRoomDTO;
import com.honeyrest.honeyrest_host.repositoryAdmin.CompanyRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.SalesStatRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection.TopRoomRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    private final UserService userService;
    private final CompanyRepository companyRepository;
    private final AccommodationRepository accommodationRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    // 대시보드 전용 쿼리(Top-N 등)
    private final SalesStatRepository stats;

    // 차트 전용 서비스(일/주/월/요일)
    private final SalesChartService salesChartService;

    @Override
    public DashboardDTO getCountsFor(String email) {
        AdminLoginRequestDTO admin = userService.getUserByEmail(email);
        if (admin == null) {
            return DashboardDTO.builder()
                    .accCount(0).resCount(0).roomCount(0).build();
        }

        long accCount = 0L;
        long resCount = 0L;
        long roomCount = 0L;

        if ("SUPER_ADMIN".equals(admin.getRole())) {
            accCount = accommodationRepository.count();
            resCount = reservationRepository.countActiveAll();
            roomCount = roomRepository.count();
        } else if ("COMPANY_ADMIN".equals(admin.getRole())) {
            Integer companyId = companyRepository.findCompanyIdByUserEmail(admin.getEmail()).orElse(null);
            if (companyId != null) {
                accCount = accommodationRepository.countByCompany_CompanyId(companyId);
                resCount = reservationRepository.countActiveByCompanyId(companyId);
                roomCount = roomRepository.countByCompanyId(companyId);
            }
        }

        return DashboardDTO.builder()
                .accCount(accCount)
                .resCount(resCount)
                .roomCount(roomCount)
                .build();
    }

    @Override
    public AdminLoginRequestDTO getCurrentAdmin(String email) {
        return userService.getUserByEmail(email);
    }

    @Override
    public List<TopRoomDTO> getTopRooms(List<Long> accommodationIds, LocalDate from, LocalDate to, int limit) {
        List<Object[]> rows = stats.findTopRoomsByAccommodations(accommodationIds, from, to, limit);

        List<TopRoomDTO> out = new ArrayList<>();
        for (Object[] r : rows) {
            Long roomId = ((Number) r[0]).longValue();
            String roomName = (String) r[1];
            String accommodationName = (String) r[2];
            int totalOrders = ((Number) r[3]).intValue();
            BigDecimal totalSales = (BigDecimal) r[4];

            out.add(TopRoomDTO.builder()
                    .roomId(roomId)
                    .roomName(roomName)
                    .accommodationName(accommodationName)
                    .totalOrders(totalOrders)
                    .totalSales(totalSales == null ? BigDecimal.ZERO : totalSales)
                    .build());
        }
        return out;
    }

    @Override
    public List<DailySalesDTO> getRecentDailyForAccommodations(List<Long> accIds, int days) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(days - 1);

        // 차트 전용 서비스 호출 → 화면에서 쓰던 DTO로 변환
        List<SalesChartPointDTO> chart = salesChartService.getChart("daily", accIds, from, to);

        return chart.stream()
                .map(p -> DailySalesDTO.builder()
                        // label이 "yyyy-MM-dd" 형식이라는 가정
                        .date(LocalDate.parse(p.getLabel()))
                        .totalSales(p.getTotalSales())
                        .totalOrders(p.getTotalOrders())
                        .avgOrderPrice(p.getAvgOrderPrice())
                        .build())
                .toList();
    }

    @Override
    public List<MonthlySalesDTO> getRecentMonthly12ForAccommodations(List<Long> accommodationIds) {
        YearMonth endYm = YearMonth.now();             // 이번 달
        YearMonth startYm = endYm.minusMonths(11);     // 12개월 전

        LocalDate from = startYm.atDay(1);             // 시작: 12개월 전 1일
        LocalDate to = endYm.atEndOfMonth();           // 끝: 이번 달 말일

        List<Object[]> rows = stats.findMonthly(accommodationIds, from, to);

        // 결과 매핑
        Map<YearMonth, BigDecimal> map = new HashMap<>();
        for (Object[] r : rows) {
            String ymStr = (String) r[0];                   // "yyyy-MM"
            BigDecimal total = (BigDecimal) r[1];
            YearMonth ym = YearMonth.parse(ymStr);
            map.put(ym, total != null ? total : BigDecimal.ZERO);
        }

        // 빠진 달은 0으로 채움
        List<MonthlySalesDTO> out = new ArrayList<>();
        for (YearMonth ym = startYm; !ym.isAfter(endYm); ym = ym.plusMonths(1)) {
            out.add(MonthlySalesDTO.builder()
                    .ym(ym)
                    .total(map.getOrDefault(ym, BigDecimal.ZERO))
                    .build());
        }
        return out;
    }
}

