package com.honeyrest.honeyrest_host.controllerAdmin;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/auth")
public class AdminAuthPageController {

    @GetMapping("/login")
    public String loginPage() {
        return "/admin/auth/login";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "/admin/auth/logout";
    }
}
