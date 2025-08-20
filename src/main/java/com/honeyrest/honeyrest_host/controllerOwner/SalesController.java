package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.DaySalesDTO;
import com.honeyrest.honeyrest_host.dtoOwner.MonthSalesDTO;
import com.honeyrest.honeyrest_host.service.ReservationService;
import com.honeyrest.honeyrest_host.service.SalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/owner")
public class SalesController {
    private final ReservationService reservationService;
    private final SalesService salesService;

    @GetMapping("/sales/day")
    public String dailySales(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Model model) {

        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1);
            endDate = now.withDayOfMonth(now.lengthOfMonth());
        }

        // daySales 조회 로직 (startDate ~ endDate 범위)
        List<DaySalesDTO> daySales = salesService.getDaySales(startDate, endDate);

        model.addAttribute("daySales", daySales);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        // 합계 등 추가 모델 속성 추가

        return "/owner/sales/day";
    }



    @GetMapping("/sales/month")
    public String monthSales(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Model model) {

        if (startDate == null || endDate == null) {
            // 올해 전체: 1월 1일 ~ 12월 31일
            int year = LocalDate.now().getYear();
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);
        }

        // 월별 합계 조회 (MonthSalesDTO: date=YYYY-MM-DD, monthPrice, quantity)
        List<MonthSalesDTO> monthSales = salesService.getMonthSales(startDate, endDate);

        model.addAttribute("monthSales", monthSales);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "owner/sales/month";
    }

}
