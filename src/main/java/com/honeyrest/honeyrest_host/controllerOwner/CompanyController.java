package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/company/list")
    public String company(Model model) {
        model.addAttribute("companies", companyService.getAllCompanies());
        return "owner/company/list";
    }

    @GetMapping("/company/create")
    public String createCompany(Model model) {
        return "owner/company/create";
    }
    @PostMapping("/company/create")
    public String createCompany(@ModelAttribute CompanyDTO companyDTO) {
        companyService.registerCompany(companyDTO);
        return "redirect:/owner/company/list";
    }
}
