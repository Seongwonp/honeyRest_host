package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.DaySalesDTO;
import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
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

    @GetMapping("/sales/list")
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
        model.addAttribute("startDate", startDate.toString());
        model.addAttribute("endDate", endDate.toString());

        // 합계 등 추가 모델 속성 추가

        return "owner/sales/list";
    }

}
