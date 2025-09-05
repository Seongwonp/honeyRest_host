package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.reports.*;

import com.honeyrest.honeyrest_host.serviceAdmin.SalesStatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import java.time.LocalDate;

import java.util.List;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@Log4j2
public class SalesReportController {

    private final SalesStatService service;


    @GetMapping("/sales")
    public String salesReportPage() {
        return "admin/reports/sales"; // templates/admin/reports/sales.html
    }

    // ===================== Payment 기준 =====================
    @GetMapping(value = "/payment/daily", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SalesStatDTO> paymentDaily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getDailyPaymentSales(from, to);
    }

    @GetMapping(value = "/payment/weekly", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SalesStatDTO> paymentWeekly(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getWeeklyPaymentSales(from, to);
    }

    @GetMapping(value = "/payment/monthly", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SalesStatDTO> paymentMonthly(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getMonthlyPaymentSales(from, to);
    }

    @GetMapping(value = "/payment/weekday", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SalesStatDTO> paymentWeekday(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getWeekdayPaymentSales(from, to);
    }

    // ===================== Reservation 기준 =====================
    @GetMapping(value = "/reservation/daily", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SalesStatDTO> reservationDaily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getDailyReservationSales(from, to);
    }

    @GetMapping(value = "/reservation/weekly", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SalesStatDTO> reservationWeekly(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getWeeklyReservationSales(from, to);
    }

    @GetMapping(value = "/reservation/monthly", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SalesStatDTO> reservationMonthly(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getMonthlyReservationSales(from, to);
    }

    @GetMapping(value = "/reservation/weekday", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SalesStatDTO> reservationWeekday(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getWeekdayReservationSales(from, to);
    }
}
