package com.honeyrest.honeyrest_host.controllerOwner.dashboard;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CouponDTO;
import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
import com.honeyrest.honeyrest_host.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class DashboardController {
    private final CompanyService companyService;
    private final AccommodationService accommodationService;
    private final RoomService roomService;
    private final CouponService couponService;
    private final ReservationService reservationService;
    private final RegionService regionService;
    private final AccommodationCategory accommodationCategory;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("rooms", roomService.getAllRooms());

        return "owner/dashboard/dashboard";
    }




    @GetMapping("/coupon/list")
    public String coupons(Model model) {
        model.addAttribute("coupons", couponService.getCoupons());
        return "owner/coupon/list";
    }

    @GetMapping("/coupon/create")
    public String createCoupon(Model model) {
        return "owner/coupon/create";
    }
    @PostMapping("/coupon/create")
    public String createCoupon(@ModelAttribute CouponDTO couponDTO) {
        couponService.registerCoupon(couponDTO);
        return "redirect:/owner/coupon/list";
    }

    @GetMapping("/review/list")
    public String reviews(Model model) {
        return "owner/review/list";
    }

    @GetMapping("/reservation/accommodations")
    public String reservations(Model model) {
        model.addAttribute("accommodationId", 0);
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("reservations", reservationService.getReservations());
        return "/owner/reservation/accommodation";
    }

    @GetMapping("/reservation/accommodation/{accommodationId}")
    public String accommodationReservations(@PathVariable Long accommodationId, Model model) {
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();
        List<ReservationDTO> reservations;
        if (accommodationId != null) {
            reservations = reservationService.getReservationsByAccommodationId(accommodationId);
            AccommodationDTO accommodation = accommodationService.getByAccommodationId(accommodationId);
            model.addAttribute("reservation", reservations);
            model.addAttribute("accommodation", accommodation);
        } else {
            reservations = reservationService.getReservations();
        }
        model.addAttribute("reservations", reservations);
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("accommodationId", accommodationId);
        return "/owner/reservation/accommodation";
    }

    @GetMapping("/reservation/companies")
    public String companies(Model model) {
        model.addAttribute("companyId", 0);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("reservations", reservationService.getReservations());
        return "/owner/reservation/company";
    }

    @GetMapping("/reservation/company/{companyId}")
    public String companyReservations(@PathVariable Long companyId, Model model) {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        List<ReservationDTO> reservations;
        if (companyId != null) {
            reservations = reservationService.getReservationsByCompanyId(companyId);
            CompanyDTO company = companyService.getCompany(companyId);
            model.addAttribute("reservation", reservations);
            model.addAttribute("company", company);
        } else {
            reservations = reservationService.getReservations();
        }
        model.addAttribute("reservations", reservations);
        model.addAttribute("companies", companies);
        model.addAttribute("companyId", companyId);
        return "/owner/reservation/company";
    }

}
