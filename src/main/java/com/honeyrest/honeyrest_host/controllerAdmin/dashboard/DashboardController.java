package com.honeyrest.honeyrest_host.controllerAdmin.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        return "/admin/dashboard/dashboard";
    }
}
