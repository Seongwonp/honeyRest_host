package com.honeyrest.honeyrest_host.controllerAdmin;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/auth")
public class AdminAuthPageController {

    @GetMapping("/login")
    public String loginPage() {
        return "admin/auth/login";
    }
    @GetMapping("/logout")
    public String logoutSuccess(RedirectAttributes ra) {
        ra.addFlashAttribute("logoutMessage", "로그아웃되었습니다.");
        return "redirect: /admin/auth/login";
    }

}
