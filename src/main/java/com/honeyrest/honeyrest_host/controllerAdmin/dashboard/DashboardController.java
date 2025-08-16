package com.honeyrest.honeyrest_host.controllerAdmin.dashboard;

import com.honeyrest.honeyrest_host.dto.AdminPrincipalDTO;
import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.ReservationService;
import lombok.RequiredArgsConstructor;
import com.honeyrest.honeyrest_host.security.AdminPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String dashboard(org.springframework.security.core.Authentication authentication, Model model) {
        // 인증 객체가 없거나 principal 타입이 Long이 아닌 경우 → 로그인 페이지
        if (authentication == null || !(authentication.getPrincipal() instanceof Long adminId)) {
            return "redirect:/admin/auth/login";
        }

        int accCount = accommodationService.getAll().size();

        PageRequestDTO pageReq = PageRequestDTO.builder().page(1).size(1).build();
        int resCount = reservationService.getReservationsByStatus("ALL", pageReq).getTotal();

        var admin = userRepository.findById(adminId).orElse(null);
        String adminName = (admin != null) ? admin.getName() : "관리자";
        String adminRole = (admin != null && admin.getRoleType() != null) ? admin.getRoleType().name() : "-";

        model.addAttribute("accCount", accCount);
        model.addAttribute("resCount", resCount);
        model.addAttribute("adminName", adminName);
        model.addAttribute("adminRole", adminRole);

        return "admin/dashboard/dashboard";
    }
}