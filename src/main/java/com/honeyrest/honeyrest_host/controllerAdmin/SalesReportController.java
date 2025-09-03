package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dto.reports.*;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyService;
import com.honeyrest.honeyrest_host.serviceAdmin.SalesStatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@Log4j2
public class SalesReportController {

    private final SalesStatService salesStatService;
    private final CompanyService companyService; // email → companyId

    /**
     * 일별 매출 그래프 페이지
     * 예: GET /admin/reports/daily
     */
    @GetMapping("/daily")
    public String dailyPage() {
        return "admin/reports/daily"; // ← Thymeleaf 템플릿 경로
    }
    /** 일자별 승인매출 (그래프 데이터) */
    @GetMapping("/daily-sales")
    @ResponseBody
    public List<SalesStatDTO> dailySalesApi(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        // 1) 로그인 사용자 확인
        if (authentication == null) return Collections.emptyList();
        Long companyId = companyService.getCompanyIdByUserEmail(authentication.getName());
        if (companyId == null) return Collections.emptyList();

        LocalDate today = LocalDate.now();
        if (to == null)   to = today;
        if (from == null) from = to.minusDays(6);
        if (from.isAfter(to)) { var tmp = from; from = to; to = tmp; }

        return salesStatService.getDailySales(companyId, from, to, true);
    }

    /** Top 매출 숙소 TOP-N */
    @GetMapping("/top-accommodations")
    @ResponseBody
    public List<TopAccommodationDTO> topAccommodationsApi(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "5") int limit
    ) {
        if (authentication == null) return Collections.emptyList();
        Long companyId = companyService.getCompanyIdByUserEmail(authentication.getName());
        if (companyId == null) return Collections.emptyList();

        LocalDate today = LocalDate.now();
        if (to == null)   to = today;
        if (from == null) from = to.minusDays(29);
        if (from.isAfter(to)) { var tmp = from; from = to; to = tmp; }

        return salesStatService.getTopAccommodations(companyId, from, to, limit);
    }

    /** 오늘(또는 지정일) 체크인 예정 목록 */
    @GetMapping("/upcoming-checkins")
    @ResponseBody
    public List<UpcomingCheckinDTO> upcomingCheckinsApi(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (authentication == null) return Collections.emptyList();
        Long companyId = companyService.getCompanyIdByUserEmail(authentication.getName());
        if (companyId == null) return Collections.emptyList();

        if (date == null) date = LocalDate.now();
        return salesStatService.getUpcomingCheckins(companyId, date, size);
    }

    /** 취소 요약 (기간 취소율) */
    @GetMapping("/cancellations/summary")
    @ResponseBody
    public CancelSummaryDTO cancelSummaryApi(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (authentication == null) return CancelSummaryDTO.empty();
        Long companyId = companyService.getCompanyIdByUserEmail(authentication.getName());
        if (companyId == null) return CancelSummaryDTO.empty();

        LocalDate today = LocalDate.now();
        if (to == null)   to = today;
        if (from == null) from = to.minusDays(29);
        if (from.isAfter(to)) { var tmp = from; from = to; to = tmp; }

        return salesStatService.getCancellationSummary(companyId, from, to);
    }

    /** 점유율/ADR/RevPAR */
    @GetMapping("/occupancy")
    @ResponseBody
    public OccupancyDTO occupancyApi(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (authentication == null) return OccupancyDTO.empty();
        Long companyId = companyService.getCompanyIdByUserEmail(authentication.getName());
        if (companyId == null) return OccupancyDTO.empty();

        LocalDate today = LocalDate.now();
        if (to == null)   to = today;
        if (from == null) from = to.minusDays(6);
        if (from.isAfter(to)) { var tmp = from; from = to; to = tmp; }

        return salesStatService.getOccupancy(companyId, from, to);
    }
}
