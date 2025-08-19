package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
import com.honeyrest.honeyrest_host.service.ReservationService;
import com.honeyrest.honeyrest_host.service.SalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String list(Model model) {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1)    ;   // "2025-08-01"
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth()); // "2025-08-31"
        model.addAttribute("daySales", salesService.getDaySales(firstDay, lastDay));
        return "owner/sales/list";
    }

}
