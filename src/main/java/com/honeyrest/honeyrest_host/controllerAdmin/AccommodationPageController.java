package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationResponseDTO;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/accommodations")
public class AccommodationPageController {

    private final AccommodationService accommodationService;

    /** 등록 화면 */
    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("form", new AccommodationCreateRequestDTO());
        return "admin/accommodations/add";
    }

    /** 등록 제출 (승인상태는 서비스에서 PENDING으로 고정됨) */
    @PostMapping("/add")
    public String addSubmit(@ModelAttribute("form") @Valid AccommodationCreateRequestDTO form) {
        accommodationService.create(form);
        return "redirect:/admin/accommodations/list";
    }

    /** 내 숙소 목록 (회사/승인상태 필터) */
    @GetMapping("/list")
    public String listPage(@RequestParam(required = false) Long companyId,
                           @RequestParam(required = false) String status,
                           Model model) {

        // 1) 전체 조회 (서비스 인터페이스 유지)
        List<AccommodationResponseDTO> all = accommodationService.getAll();

        // 2) 컨트롤러에서 간단 필터 (회사/승인상태)
        List<AccommodationResponseDTO> filtered = all.stream()
                .filter(dto -> companyId == null || companyId.equals(dto.getCompanyId()))
                .filter(dto -> status == null || status.isBlank() || status.equalsIgnoreCase(dto.getStatus()))
                .sorted(Comparator.comparing(AccommodationResponseDTO::getAccommodationId).reversed())
                .toList();

        model.addAttribute("items", filtered);
        model.addAttribute("companyId", companyId);
        model.addAttribute("status", status);
        return "admin/accommodations/list";
    }

    /** 상세 보기 */
    @GetMapping("/{id}")
    public String detailPage(@PathVariable Long id, Model model) {
        AccommodationResponseDTO dto = accommodationService.getById(id);
        model.addAttribute("item", dto);
        return "admin/accommodations/detail";
    }

    /** ✅ 총관리자에게 승인요청 (DRAFT/REJECTED → PENDING) */
    @PostMapping("/{id}/request")
    public String requestApproval(@PathVariable Long id,
                                  @RequestParam(required = false, defaultValue = "PENDING") String to) {
        accommodationService.changeStatus(id, to); // "PENDING"
        return "redirect:/admin/accommodations/list?status=PENDING";
    }

    /** (선택) 다건 승인요청 */
    @PostMapping("/request")
    public String requestApprovalBulk(@RequestParam("ids") List<Long> ids) {
        ids.forEach(i -> accommodationService.changeStatus(i, "PENDING"));
        return "redirect:/admin/accommodations/list?status=PENDING";
    }
}
