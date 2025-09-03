package com.honeyrest.honeyrest_host.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/auth/login")
    public String loginPage() {
        // templates/auth/login.html 반환
        return "auth/login";
    }
}