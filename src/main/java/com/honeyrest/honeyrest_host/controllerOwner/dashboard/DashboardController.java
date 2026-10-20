package com.honeyrest.honeyrest_host.controllerOwner.dashboard;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.serviceOwner.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
    private final OUserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/auth/login";
        }

        String email = (authentication.getPrincipal() instanceof String s) ? s : authentication.getName();

        UserDTO owner = userService.findByEmail(email);
        if (owner == null) return "redirect:/auth/login";

        model.addAttribute("companyCount", companyService.getAllCompanies().size());
        model.addAttribute("accommodationCount", accommodationService.getAllAccommodations().size());
        model.addAttribute("roomCount", roomService.getAllRooms().size());
        model.addAttribute("reservationCount", reservationService.getReservationsByActive().size());
        model.addAttribute("userCount", userService.countUsers());
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
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        return "owner/coupon/create";
    }

    @PostMapping("/coupon/create")
    public String createCoupon(@ModelAttribute CouponDTO couponDTO, RedirectAttributes ra) {
        try {
            couponService.registerCoupon(couponDTO);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/owner/coupon/create";
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 쿠폰 코드는 unique
            ra.addFlashAttribute("error", "이미 사용 중인 쿠폰 코드입니다.");
            return "redirect:/owner/coupon/create";
        }
        ra.addFlashAttribute("success", "쿠폰을 등록했습니다.");
        return "redirect:/owner/coupon/list";
    }


}
