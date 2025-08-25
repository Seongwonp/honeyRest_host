package com.honeyrest.honeyrest_host.controllerAdmin.dashboard;


import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import com.honeyrest.honeyrest_host.repository.ReservationRepository;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Log4j2
public class DashboardController {

    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;
    private final ReservationRepository reservationRepository;
    private final CompanyRepository companyRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/admin/auth/login";
        }

        String email = (authentication.getPrincipal() instanceof String s) ? s : authentication.getName();

        var admin = userRepository.findByEmail(email).orElse(null);
        if (admin == null) return "redirect:/admin/auth/login";

        long accCount;
        long resCount;

        if (admin.getRole() == "SUPER_ADMIN") {
            // 전체 합계
            accCount = accommodationRepository.count();
            resCount = reservationRepository.countActiveAll(); // 취소 제외 예시
        } else if (admin.getRole() =="COMPANY_ADMIN") {
            Optional<Long> companyIdOpt = companyRepository.findCompanyIdByUserEmail(admin.getEmail());
            Long companyId = companyIdOpt.orElse(null); // ← Optional 처리
            log.info("companyId from email: {}", companyId);
            if (companyId == null) {
                accCount = 0;
                resCount = 0;
            } else {
                accCount = accommodationRepository.countByCompany_CompanyId(companyId);
                resCount = reservationRepository.countActiveByCompanyId(companyId);
            }
        } else {
            // 그 외 ROLE이면 필요에 맞게
            accCount = 0;
            resCount = 0;
        }

        model.addAttribute("accCount", accCount);
        model.addAttribute("resCount", resCount);
        model.addAttribute("currentAdmin", admin);

        return "admin/dashboard/dashboard";
    }
}