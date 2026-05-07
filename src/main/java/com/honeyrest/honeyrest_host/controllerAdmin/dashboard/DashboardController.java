package com.honeyrest.honeyrest_host.controllerAdmin.dashboard;


import com.honeyrest.honeyrest_host.dtoAdmin.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.DashboardDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.SalesChartPointDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.*;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyService;
import com.honeyrest.honeyrest_host.serviceAdmin.DashboardReportService;
import com.honeyrest.honeyrest_host.serviceAdmin.DashboardService;
import com.honeyrest.honeyrest_host.serviceAdmin.SalesChartService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller("adminDashboardController")
@RequestMapping("/admin")
@RequiredArgsConstructor
@Log4j2
public class DashboardController {

    private final DashboardService dashboardService;
    private final AccommodationService accommodationService;
    private final SalesChartService salesChartService;
    private final DashboardReportService dashboardReportService;
    private final CompanyService companyService;


    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();

        AdminLoginRequestDTO admin = dashboardService.getCurrentAdmin(email);
        if (admin == null) return "redirect:/auth/login";

        DashboardDTO counts = dashboardService.getCountsFor(email);
        model.addAttribute("accCount", counts.getAccCount());
        model.addAttribute("resCount", counts.getResCount());
        model.addAttribute("roomCount", counts.getRoomCount());
        model.addAttribute("currentAdmin", admin);

        var company = companyService.getByUserEmail(email);
        model.addAttribute("companyName", company != null ? company.getName() : "");

        List<Long> accIds = accommodationService.getAccommodationIdsByAdminEmail(email);

        LocalDate to = LocalDate.now();
        LocalDate from30 = to.minusDays(29);
        LocalDate from12m = to.minusMonths(11).withDayOfMonth(1);

        // 1) salesDaily30 : 최근 30일 일별 매출
        List<DailySalesDTO> salesDaily30 =
                dashboardService.getRecentDailyForAccommodations(accIds, 30);
        model.addAttribute("salesDaily30", salesDaily30);

        // 2) salesMonthly12 : 최근 12개월 월별 매출
        List<MonthlySalesDTO> salesMonthly12 =
                dashboardService.getRecentMonthly12ForAccommodations(accIds);
        model.addAttribute("salesMonthly12", salesMonthly12);

        // 3) topRooms : 최근 30일 Top-N 방 매출
        List<TopRoomDTO> topRooms =
                dashboardService.getTopRooms(accIds, from30, to, 5);
        model.addAttribute("topRooms", topRooms);

        // 4) occ : 점유율/ADR/RevPAR/취소율 (최근 30일 기준 예시)
        OccupancyDTO occ =
                dashboardReportService.getOccupancy(accIds, from30, to);
        model.addAttribute("occ", occ);

        // 5) upcomingList : 오늘 체크인 예정
        List<UpcomingCheckinDTO> upcomingList =
                dashboardReportService.getTodayCheckins(accIds, to);
        model.addAttribute("upcomingList", upcomingList);

        return "admin/dashboard/dashboard";
    }
}