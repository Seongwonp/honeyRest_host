package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.DaySalesDTO;
import com.honeyrest.honeyrest_host.dtoOwner.MonthSalesDTO;
import com.honeyrest.honeyrest_host.serviceOwner.OAccommodationService;
import com.honeyrest.honeyrest_host.serviceOwner.OCompanyService;
import com.honeyrest.honeyrest_host.serviceOwner.OReservationService;
import com.honeyrest.honeyrest_host.serviceOwner.OSalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller("ownerSalesController")
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/owner")
public class SalesController {
    private final OReservationService reservationService;
    private final OSalesService salesService;
    private final OCompanyService companyService;
    private final OAccommodationService accommodationService;

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

        List<CompanyDTO> companies = companyService.getAllCompanies();
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        model.addAttribute("companies", companies);
        model.addAttribute("accommodations", accommodations);
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
        List<CompanyDTO> companies = companyService.getAllCompanies();
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        model.addAttribute("companies", companies);
        model.addAttribute("accommodations", accommodations);

        model.addAttribute("monthSales", monthSales);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);


        return "owner/sales/month";
    }

    @GetMapping("/sales/day/company/{companyId}")
    public String companyDailySales(
            @PathVariable Long companyId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            // 추가: 검색 조건 파라미터 받기
            @RequestParam(required = false) Long accommodationId,
            Model model) {

        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1);
            endDate = now.withDayOfMonth(now.lengthOfMonth());
        }

        // daySales 조회 로직 (startDate ~ endDate 범위)
        List<DaySalesDTO> daySales = salesService.getCompanyDaySales(companyId, startDate, endDate);

        model.addAttribute("company" , companyService.getCompany(companyId));
        List<CompanyDTO> companies = companyService.getAllCompanies();
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        model.addAttribute("companies", companies);
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("daySales", daySales);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        // 추가: 검색 조건 모델에 담기 (현재 선택값 유지용)
        model.addAttribute("selectedCompanyId", companyId);
        model.addAttribute("selectedAccommodationId", accommodationId);
        return "/owner/sales/day";
    }

    @GetMapping("/sales/month/company/{companyId}")
    public String companyMonthSales(
            @PathVariable Long companyId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            // 추가: 검색 조건 파라미터 받기
            @RequestParam(required = false) Long accommodationId,

            Model model) {
        if (startDate == null || endDate == null) {
            // 올해 전체: 1월 1일 ~ 12월 31일
            int year = LocalDate.now().getYear();
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);
        }

        // 월별 합계 조회 (MonthSalesDTO: date=YYYY-MM-DD, monthPrice, quantity)
        List<MonthSalesDTO> monthSales = salesService.getCompanyMonthSales(companyId, startDate, endDate);
        List<CompanyDTO> companies = companyService.getAllCompanies();
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        model.addAttribute("companies", companies);
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("company" , companyService.getCompany(companyId));
        model.addAttribute("monthSales", monthSales);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        // 추가: 검색 조건 모델에 담기 (현재 선택값 유지용)
        model.addAttribute("selectedCompanyId", companyId);
        model.addAttribute("selectedAccommodationId", accommodationId);
        return "owner/sales/month";
    }

    @GetMapping("/sales/day/accommodation/{accommodationId}")
    public String accommodationDailySales(
            @PathVariable Long accommodationId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            // 추가: 검색 조건 파라미터 받기
            Model model) {

        Long companyId = companyService.getCompanyIdByAccommodationId(accommodationId);
        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1);
            endDate = now.withDayOfMonth(now.lengthOfMonth());
        }
        // daySales 조회 로직 (startDate ~ endDate 범위)
        List<DaySalesDTO> daySales = salesService.getAccommodationDaySales(accommodationId, startDate, endDate);
        List<CompanyDTO> companies = companyService.getAllCompanies();
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        model.addAttribute("companies", companies);
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("accommodation" , accommodationService.getByAccommodationId(accommodationId));
        model.addAttribute("daySales", daySales);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        // 추가: 검색 조건 모델에 담기 (현재 선택값 유지용)
        model.addAttribute("selectedCompanyId", companyId);
        model.addAttribute("selectedAccommodationId", accommodationId);
        return "/owner/sales/day";
    }

    @GetMapping("/sales/month/accommodation/{accommodationId}")
    public String accommodationMonthlySales(
            @PathVariable Long accommodationId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            // 추가: 검색 조건 파라미터 받기
            Model model){
        Long companyId = companyService.getCompanyIdByAccommodationId(accommodationId);

        if (startDate == null || endDate == null) {
            // 올해 전체: 1월 1일 ~ 12월 31일
            int year = LocalDate.now().getYear();
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);
        }

        // 월별 합계 조회 (MonthSalesDTO: date=YYYY-MM-DD, monthPrice, quantity)
        List<MonthSalesDTO> monthSales = salesService.getAccommodationMonthSales(accommodationId, startDate, endDate);
        List<CompanyDTO> companies = companyService.getAllCompanies();
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        model.addAttribute("companies", companies);
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("accommodation" , accommodationService.getByAccommodationId(accommodationId));
        model.addAttribute("monthSales", monthSales);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        // 추가: 검색 조건 모델에 담기 (현재 선택값 유지용)

        model.addAttribute("selectedCompanyId", companyId);
        model.addAttribute("selectedAccommodationId", accommodationId);

        return "owner/sales/month";
    }

}
