package com.honeyrest.honeyrest_host.controllerAdmin;



import com.honeyrest.honeyrest_host.dtoAdmin.DashboardDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.SalesChartPointDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.DashboardService;
import com.honeyrest.honeyrest_host.serviceAdmin.SalesChartService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/reports")
@Log4j2
@RequiredArgsConstructor
public class ReportController {


    private final AccommodationService accommodationService;
    private final SalesChartService salesChartService;
    private final DashboardService dashboardService;

    // 페이지 진입 (타임리프)
    @GetMapping("/sales")
    public String sales(Authentication auth, Model model) {
        String email = auth.getName();

        // KPI
        DashboardDTO counts = dashboardService.getCountsFor(email);
        model.addAttribute("accCount", counts.getAccCount());
        model.addAttribute("resCount", counts.getResCount());
        model.addAttribute("roomCount", counts.getRoomCount());
        model.addAttribute("currentAdmin", dashboardService.getCurrentAdmin(email));

        // 초기 차트 데이터(일별 7일 / 월별 6개월)
        List<Long> accIds = accommodationService.getAccommodationIdsByAdminEmail(email);
        LocalDate to = LocalDate.now();
        LocalDate from7  = to.minusDays(6);
        LocalDate from6m = to.minusMonths(5).withDayOfMonth(1);

        List<SalesChartPointDTO> daily7   = salesChartService.getChart("daily",   accIds, from7,  to);
        List<SalesChartPointDTO> monthly6 = salesChartService.getChart("monthly", accIds, from6m, to);

        model.addAttribute("daily", daily7);
        model.addAttribute("monthly", monthly6);

        return "admin/reports/sales";
    }

    /** AJAX 엔드포인트 (JSON) */
    @GetMapping("/chart")
    @ResponseBody
    public List<SalesChartPointDTO> chart(Authentication auth,
                                          @RequestParam String mode,
                                          @RequestParam LocalDate from,
                                          @RequestParam LocalDate to) {
        List<Long> accIds = accommodationService.getAccommodationIdsByAdminEmail(auth.getName());
        return salesChartService.getChart(mode, accIds, from, to);
    }
}