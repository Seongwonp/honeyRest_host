package com.honeyrest.honeyrest_host.controllerOwner.dashboard;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import com.honeyrest.honeyrest_host.serviceOwner.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller("ownerDashboardController")
@RequestMapping("/owner")
@RequiredArgsConstructor
public class DashboardController {
    private final OCompanyService companyService;
    private final OAccommodationService accommodationService;
    private final ORoomService roomService;
    private final OCouponService couponService;
    private final OReservationService reservationService;
    private final OReviewService reviewService;
    private final OUserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/auth/login";
        }

        String email = (authentication.getPrincipal() instanceof String s) ? s : authentication.getName();

        var owner = userRepository.findByEmail(email);
        if (owner == null) return "redirect:/auth/login";

        model.addAttribute("companyCount", companyService.getAllCompanies().size());
        model.addAttribute("accommodationCount", accommodationService.getAllAccommodations().size());
        model.addAttribute("roomCount", roomService.getAllRooms().size());
        model.addAttribute("reservationCount", reservationService.getReservationsByActive().size());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("currentAdmin", owner);

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


}
