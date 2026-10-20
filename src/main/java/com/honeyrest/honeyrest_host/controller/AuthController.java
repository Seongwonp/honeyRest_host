package com.honeyrest.honeyrest_host.controller;

import com.honeyrest.honeyrest_host.config.DemoLoginProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@EnableConfigurationProperties(DemoLoginProperties.class)
public class AuthController {

    private final DemoLoginProperties demoLoginProperties;

    @GetMapping("/auth/login")
    public String loginPage(Model model) {
        // demo 프로필이면 데모 계정을 안내한다
        model.addAttribute("demoAccounts", demoLoginProperties.visibleAccounts());
        // templates/auth/login.html 반환
        return "auth/login";
    }
}
