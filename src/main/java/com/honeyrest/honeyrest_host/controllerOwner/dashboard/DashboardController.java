package com.honeyrest.honeyrest_host.controllerOwner.dashboard;

import com.honeyrest.honeyrest_host.dtoOwner.CouponDTO;
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


    @GetMapping("/reservation/list")
    public String reservations(Model model) {
        model.addAttribute("reservations", reservationService.getReservations());
        return "owner/reservation/list";
    }


}
