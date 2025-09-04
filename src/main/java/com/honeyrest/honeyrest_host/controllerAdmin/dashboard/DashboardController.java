package com.honeyrest.honeyrest_host.controllerAdmin.dashboard;


import com.honeyrest.honeyrest_host.dto.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dto.DashboardDTO;
import com.honeyrest.honeyrest_host.repositoryAdmin.CompanyRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.serviceAdmin.DashboardService;
import com.honeyrest.honeyrest_host.serviceAdmin.UserService;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller("adminDashboardController")
@RequestMapping("/admin")
@RequiredArgsConstructor
@Log4j2
public class DashboardController {

    private final DashboardService dashboardService; // Service만 의존

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {

        String email = authentication.getName(); // Authentication 에서 email 추출

        AdminLoginRequestDTO admin = dashboardService.getCurrentAdmin(email);
        if (admin == null) {
            return "redirect:/auth/login";
        }

        DashboardDTO counts = dashboardService.getCountsFor(email);

        // 뷰에서 사용할 데이터 모델에 담기
        model.addAttribute("accCount", counts.getAccCount());
        model.addAttribute("resCount", counts.getResCount());
        model.addAttribute("roomCount", counts.getRoomCount());
        model.addAttribute("currentAdmin", admin);

        return "admin/dashboard/dashboard";
    }

}