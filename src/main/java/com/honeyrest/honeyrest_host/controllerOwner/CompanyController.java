package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.type.CompanyStatus;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.serviceOwner.OAccommodationService;
import com.honeyrest.honeyrest_host.serviceOwner.OCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
public class CompanyController {
    private final OCompanyService companyService;
    private final OAccommodationService accommodationService;

    @GetMapping("/company/list")
    public String companyList(
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) Long accommodationId,
            @RequestParam(required = false) String keyword,
            @ModelAttribute PageRequestDTO pageRequestDTO,
            Model model) {
        // 검색창(keyword)이 전달돼도 무시되던 문제 수정
        PageResponseDTO<CompanyDTO> responseDTO = companyService.getCompaniesWithPage(pageRequestDTO, keyword);
        model.addAttribute("keyword", keyword);

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
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("companies", responseDTO.getDtoList());
        return "owner/company/inActive";
    }


    @GetMapping("/company/create")
    public String createCompany(Model model) {
        return "owner/company/create";
    }

    @PostMapping("/company/create")
    public String createCompany(@ModelAttribute CompanyDTO companyDTO, RedirectAttributes ra) {
        companyService.registerCompany(companyDTO);
        ra.addFlashAttribute("success", "업체를 등록했습니다.");
        return "redirect:/owner/company/list";
    }

    @GetMapping("/company/{companyId}/modify")
    public String modifyCompany(@PathVariable("companyId") Integer companyId, Model model) {
        model.addAttribute("companyId", companyId);
        model.addAttribute("company", companyService.getCompany(companyId));
        return "owner/company/modify";
    }

    @PostMapping("/company/modify")
    public String modifyCompany(@ModelAttribute CompanyDTO companyDTO, RedirectAttributes ra) {
        companyService.modifyCompany(companyDTO);
        ra.addFlashAttribute("success", "업체 정보를 수정했습니다.");
        return CompanyStatus.isActive(companyDTO.getStatus())
                ? "redirect:/owner/company/list" : "redirect:/owner/company/inActive/list";
    }

    @PostMapping("/company/{companyId}/delete")
    public String deleteCompany(@PathVariable Integer companyId, RedirectAttributes ra) {
        companyService.removeCompany(companyId);
        ra.addFlashAttribute("success", "업체를 비활성화했습니다.");
        return "redirect:/owner/company/list";
    }

    /** 비활성 업체 재활성화 (비활성 목록에서 "삭제"를 다시 누르면 아무 변화가 없던 자리를 대신한다) */
    @PostMapping("/company/{companyId}/activate")
    public String activateCompany(@PathVariable Integer companyId, RedirectAttributes ra) {
        companyService.activateCompany(companyId);
        ra.addFlashAttribute("success", "업체를 다시 활성화했습니다.");
        return "redirect:/owner/company/inActive/list";
    }

    // 회사 검색 API (자동완성용)
    @GetMapping("/company/search")
    @ResponseBody
    public List<CompanyDTO> searchCompanies(@RequestParam String keyword) {
        return companyService.searchByNameContaining(keyword).stream().filter(c -> CompanyStatus.isActive(c.getStatus())).toList();
    }
    // 회사 검색 API (자동완성용)
    @GetMapping("/company/inActive/search")
    @ResponseBody
    public List<CompanyDTO> searchInActiveCompanies(@RequestParam String keyword) {
        return companyService.searchByNameContaining(keyword).stream().filter(c -> !CompanyStatus.isActive(c.getStatus())).toList();
    }

    // 선택한 회사의 숙소 목록 JSON
    @GetMapping("/company/{companyId}/accommodations/json")
    @ResponseBody
    public List<AccommodationDTO> getAccommodationsByCompany(@PathVariable Integer companyId) {
        return accommodationService.getAccommodationsByCompanyId(companyId);
    }

}
