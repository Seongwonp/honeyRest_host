package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
public class CompanyController {
    private final CompanyService companyService;
    private final AccommodationService accommodationService;

    @GetMapping("/company/list")
    public String companyList(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long accommodationId,
            @ModelAttribute PageRequestDTO pageRequestDTO,
            Model model) {
        if (pageRequestDTO.getPage() <= 0) pageRequestDTO.setPage(1);
        if (pageRequestDTO.getSize() <= 0) pageRequestDTO.setSize(10);

        PageResponseDTO<CompanyDTO> responseDTO = companyService.getCompaniesWithPage(pageRequestDTO);

        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("companies", responseDTO.getDtoList());
        model.addAttribute("selectedCompanyId", companyId);
        model.addAttribute("selectedAccommodationId", accommodationId);

        return "owner/company/list";
    }

    @GetMapping("/company/inActive/list")
    public String companyInActiveList(@ModelAttribute PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<CompanyDTO> responseDTO = companyService.getInActiveCompaniesWithPage(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("companies", responseDTO.getDtoList());
        model.addAttribute("inActive", 1);
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

    // 회사 검색 API (자동완성용)
    @GetMapping("/company/search")
    @ResponseBody
    public List<CompanyDTO> searchCompanies(@RequestParam String keyword) {
        return companyService.searchByNameContaining(keyword);
    }

    // 선택한 회사의 숙소 목록 JSON
    @GetMapping("/company/{companyId}/accommodations/json")
    @ResponseBody
    public List<AccommodationDTO> getAccommodationsByCompany(@PathVariable Long companyId) {
        return accommodationService.getAccommodationsByCompanyId(companyId);
    }

}
