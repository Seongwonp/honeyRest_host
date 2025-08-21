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

    @GetMapping("/company/{companyId}/modify")
    public String modifyCompany(@PathVariable("companyId") Long companyId, Model model) {
        model.addAttribute("companyId", companyId);
        model.addAttribute("company", companyService.getCompany(companyId));
        return "owner/company/modify";
    }

    @PostMapping("/company/modify")
    public String modifyCompany(@ModelAttribute CompanyDTO companyDTO) {
        companyService.modifyCompany(companyDTO);
        return "redirect:/owner/company/list";
    }

    @PostMapping("/company/{companyId}/delete")
    public String deleteCompany(@PathVariable Long companyId) {
        companyService.removeCompany(companyId);
        return "redirect:/owner/company/list";
    }
}
