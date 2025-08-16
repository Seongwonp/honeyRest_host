package com.honeyrest.honeyrest_host.controllerAdmin;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccommodationPageController {

    @GetMapping("/admin/accommodations/add")
    public String addPage() {
        return "admin/accommodations/add";
    }

    @GetMapping("/admin/accommodations/list")
    public String listPage() {
        return "admin/accommodations/list";
    }
}
