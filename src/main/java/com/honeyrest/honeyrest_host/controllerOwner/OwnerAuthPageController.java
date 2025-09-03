package com.honeyrest.honeyrest_host.controllerOwner;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/owner/auth")
public class OwnerAuthPageController {

    @GetMapping("/login")
    public String loginPage() {
        return "owner/auth/login";
    }
    @GetMapping("/logout")
    public String logoutSuccess(RedirectAttributes ra) {
        ra.addFlashAttribute("logoutMessage", "로그아웃되었습니다.");
        return "redirect:/owner/auth/login";
    }

}