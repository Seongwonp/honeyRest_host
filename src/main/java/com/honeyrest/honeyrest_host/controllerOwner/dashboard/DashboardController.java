package com.honeyrest.honeyrest_host.controllerOwner.dashboard;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.repository.AccommodationRepository;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import com.honeyrest.honeyrest_host.repository.ReservationRepository;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import com.honeyrest.honeyrest_host.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class DashboardController {
    private final CompanyService companyService;
    private final AccommodationService accommodationService;
    private final RoomService roomService;
    private final CouponService couponService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/owner/auth/login";
        }

        String email = (authentication.getPrincipal() instanceof String s) ? s : authentication.getName();

        var owner = userRepository.findByEmail(email);
        if (owner == null) return "redirect:/owner/auth/login";

        long companyCount;
        long accommodationCount;
        long roomCount;
        long reservationCount;
        if (Objects.equals(owner.getRole(), "SUPER_ADMIN")) {
            // 전체 합계
            companyCount = companyService.getAllCompanies().size();
            accommodationCount = accommodationService.getAllAccommodations().size();
            roomCount = roomService.getAllRooms().size();
            reservationCount = reservationService.getReservationsByActive().size(); // 취소 제외 예시
        } else {
            // 그 외 ROLE이면 필요에 맞게
            companyCount = 0;
            accommodationCount = 0;
            reservationCount = 0;
            roomCount = 0;
        }
        model.addAttribute("companyCount", companyCount);
        model.addAttribute("accommodationCount", accommodationCount);
        model.addAttribute("roomCount", roomCount);
        model.addAttribute("reservationCount", reservationCount);
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
