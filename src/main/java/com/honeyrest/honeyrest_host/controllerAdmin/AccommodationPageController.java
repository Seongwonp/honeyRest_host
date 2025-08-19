package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationResponseDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationUpdateRequestDTO;
import com.honeyrest.honeyrest_host.entity.AccommodationCategory;
import com.honeyrest.honeyrest_host.entity.Region;
import com.honeyrest.honeyrest_host.repository.RegionRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationCategoryRepository;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/accommodations")
public class AccommodationPageController {

    private final AccommodationService accommodationService;
    private final RegionRepository regionRepository;
    private final AccommodationCategoryRepository accommodationCategoryRepository;


    /**
     * 등록 화면
     */
    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("mainRegions", regionRepository.findByLevel(1));
        model.addAttribute("form", new AccommodationCreateRequestDTO());
        return "admin/accommodations/add";
    }

    /**
     * 등록 제출 (승인상태는 서비스에서 PENDING으로 고정됨)
     */
    @PostMapping("/add")
    public String addSubmit(@ModelAttribute("form") @Valid AccommodationCreateRequestDTO form) {
        accommodationService.create(form);
        return "redirect:/admin/accommodations/list";
    }

    @GetMapping("/list")
    public String listPage(@RequestParam(required = false) String q,
                           @RequestParam(required = false) Long categoryId,
                           @RequestParam(required = false, name = "regionId") Long mainRegionId,
                           Pageable pageable,
                           Model model) {
        var page = accommodationService.search(q, categoryId, mainRegionId,
                pageable == null ? Pageable.unpaged() : pageable);
        model.addAttribute("accommodations", page.getContent());
        return "admin/accommodations/list";
    }

    /**
     * 상세 보기
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        // DTO에 name, categoryName, regionName, minPrice, status, address, description, amenities, images, tags ...을 채워서 반환
        AccommodationResponseDTO acc = accommodationService.getById(id);
        model.addAttribute("acc", acc);
        return "admin/accommodations/detail";
    }

    /**
     * ✅ 총관리자에게 승인요청 (DRAFT/REJECTED → PENDING)
     */
    @PostMapping("/{id}/request")
    public String requestApproval(@PathVariable Long id,
                                  @RequestParam(required = false, defaultValue = "PENDING") String to) {
        accommodationService.changeStatus(id, to); // "PENDING"
        return "redirect:/admin/accommodations/list?status=PENDING";
    }

    /**
     * (선택) 다건 승인요청
     */
    @PostMapping("/request")
    public String requestApprovalBulk(@RequestParam("ids") List<Long> ids) {
        ids.forEach(i -> accommodationService.changeStatus(i, "PENDING"));
        return "redirect:/admin/accommodations/list?status=PENDING";
    }

    // 지역(대)의 하위(소) 목록 JSON
    @GetMapping(value = "/regions/children", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> regionChildren(@RequestParam("parentId") Long parentId) {
        return regionRepository.findByParentId(parentId).stream()
                .map(r -> Map.<String, Object>of(
                        "id", r.getRegionId(),
                        "name", r.getName()
                ))
                .toList();
    }


    // 수정 폼 (GET)
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        AccommodationResponseDTO dto = accommodationService.getById(id);

        // 2) 화면 폼용으로 기존 DTO 재활용
        AccommodationUpdateRequestDTO form = AccommodationUpdateRequestDTO.builder()
                .companyId(dto.getCompanyId())
                .categoryId(dto.getCategoryId())
                .mainRegionId(dto.getMainRegionId())
                .subRegionId(dto.getSubRegionId())
                .name(dto.getName())
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .thumbnailUrl(dto.getThumbnailUrl())
                .description(dto.getDescription())
                .amenities(dto.getAmenities())
                .checkInTime(dto.getCheckInTime())
                .checkOutTime(dto.getCheckOutTime())
                .status(dto.getStatus())
                .minPrice(dto.getMinPrice())
                .images(dto.getImages())
                .tagIds(dto.getTags() == null ? List.of()
                        : dto.getTags().stream().map(t -> t.getTagId()).toList())
                .build();

        // 3) 셀렉트박스 데이터
        var categories = accommodationCategoryRepository.findAll(Sort.by("name").ascending());
        var mainRegions = regionRepository.findByLevel(1);
        var subRegions = (dto.getMainRegionId() != null)
                ? regionRepository.findByParentId(dto.getMainRegionId())
                : Collections.<Region>emptyList();

        // 4) 모델
        model.addAttribute("acc", dto);        // 상단 표시용(읽기)
        model.addAttribute("form", form);      // 폼 바인딩용
        model.addAttribute("categories", categories);
        model.addAttribute("mainRegions", mainRegions);
        model.addAttribute("subRegions", subRegions);

        return "admin/accommodations/edit"; // edit.html
    }

    /**
     * 수정 제출
     */
    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id,
                             @ModelAttribute("form") AccommodationUpdateRequestDTO form,
                             RedirectAttributes ra) {
        accommodationService.update(id, form);
        ra.addAttribute("updated", "1");
        return "redirect:/admin/accommodations/list";
    }
}
