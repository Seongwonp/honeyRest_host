package com.honeyrest.honeyrest_host.controllerAdmin.dashboard;


import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.ReservationService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final AccommodationService accommodationService;
    private final ReservationService reservationService;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Long adminId)) {
            return "redirect:/admin/auth/login";
        }

        long accCount = accommodationService.count();
        long resCount = reservationService.countAll();

        var admin = userRepository.findById(adminId).orElse(null);

        model.addAttribute("accCount", accCount);
        model.addAttribute("resCount", resCount);
        model.addAttribute("currentAdmin", admin); // ✅ admin을 그대로 모델에 넣음

        return "admin/dashboard/dashboard";
    }
}