package com.honeyrest.honeyrest_host.web;

import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalViewModelAdvice {

    private final UserRepository userRepository;

    @ModelAttribute
    public void addCurrentAdmin(Model model, Authentication authentication) {
        if (model.containsAttribute("currentAdmin")) return;

        record CurrentAdmin(String name, String email, String role) {}
        CurrentAdmin fallback = new CurrentAdmin("관리자", "-", "-");

        if (authentication == null) {
            model.addAttribute("currentAdmin", fallback);
            return;
        }

        String email = authentication.getName();
        User u = userRepository.findByEmail(email);

        if (u == null) {
            model.addAttribute("currentAdmin", fallback);
            return;
        }

        model.addAttribute("currentAdmin",
                new CurrentAdmin(
                        (u.getName() == null || u.getName().isBlank()) ? "관리자" : u.getName(),
                        email,
                        (u.getRole() == null) ? "-" : u.getRole()
                )
        );
    }
}
