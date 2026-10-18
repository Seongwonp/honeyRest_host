package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.type.AccommodationStatus;



import com.honeyrest.honeyrest_host.dtoAdmin.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.RegionDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.*;
import com.honeyrest.honeyrest_host.serviceAdmin.CancellationPolicyService;
import com.honeyrest.honeyrest_host.serviceAdmin.RegionService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationCategoryService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationImageService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationRegistrationService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationStatusPolicy;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyService;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyResourceAccessService;
import com.honeyrest.honeyrest_host.serviceAdmin.UserService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationTagService;
import com.honeyrest.honeyrest_host.utilAdmin.AmenitiesParser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@Log4j2
@Controller("adminAccommodationPageController")
@RequiredArgsConstructor
@RequestMapping("/admin/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final AccommodationImageService accommodationImageService;
    private final CompanyService companyService;
    private final UserService userService;
    private final CompanyResourceAccessService resourceAccessService;
    private final AccommodationTagService accommodationTagService;
    private final CancellationPolicyService cancellationPolicyService;
    // 등록/수정 처리 흐름(업로드 보상 포함 단일 트랜잭션)
    private final AccommodationRegistrationService registrationService;
    private final AccommodationCategoryService accommodationCategoryService;
    private final RegionService regionService;


    /*
     * 등록 화면
     */
    @GetMapping("/add")
    public String addPage(Model model) {
        AccommodationCreateRequestDTO form = new AccommodationCreateRequestDTO();

        form.setTagIds(Collections.emptyList());


        model.addAttribute("mainRegions", regionService.listMainRegions());
        model.addAttribute("form", form);
        model.addAttribute("tagsByCategory", accommodationTagService.findAllGroupedByCategory());
        return "admin/accommodations/add";
    }

    /*
     * 등록 제출 (승인상태는 서비스에서 PENDING 으로 고정됨)
     */
    @PostMapping("/add")
    public String addSubmit(@ModelAttribute("form") @Valid AccommodationCreateRequestDTO form,
                            BindingResult binding,
                            @RequestParam(value = "subImages", required = false) List<MultipartFile> subImages,
                            Model model,
                            RedirectAttributes ra,
                            Authentication authentication) {
        Integer companyId = resourceAccessService.currentCompanyId(authentication);
        if (companyId == null) return "redirect:/auth/login";
        form.setCompanyId(companyId);
        // 신규 등록은 항상 승인 대기(PENDING)로 시작한다 (등록 서비스·숙소 서비스에서도 강제).
        form.setStatus(AccommodationStatusPolicy.INITIAL_STATUS);
        if (binding.hasErrors()) {
            binding.getAllErrors().forEach(err -> log.warn("bind err: {}", err));
            model.addAttribute("mainRegions", regionService.listMainRegions());
            model.addAttribute("tagsByCategory", accommodationTagService.findAllGroupedByCategory());
            return "admin/accommodations/add";
        }

        try {
            // 업로드 → 저장 → 환불 규정 → 이미지까지 한 트랜잭션 (실패 시 이번 요청에서 올린 파일 삭제)
            registrationService.register(companyId, form, subImages);
            ra.addFlashAttribute("success", "숙소 등록이 요청되었습니다.");
            return "redirect:/admin/accommodations/list?status=PENDING";
        } catch (Exception e) {
            log.error("add error", e);
            ra.addFlashAttribute("error", "등록 중 오류: " + e.getMessage());
            return "redirect:/admin/accommodations/add";
        }

    }

    /*
     * 내 명의(회사)의 숙소 목록 가져오기
     */
    @GetMapping("/list")
    public String list(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String status,
            Model model
    ) {
        // 1) 로그인 체크
        if (authentication == null || !authentication.isAuthenticated()
            || authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return "redirect:/auth/login";
        }

        // 2) 이메일(=username) 꺼내기 (UserDetails 구현체/기타 모두 커버)
        String email;
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            email = ud.getUsername();
        } else {
            email = authentication.getName();
        }

        // 3) 관리자/유저 검증
        AdminLoginRequestDTO admin = userService.getUserByEmail(email);
        if (admin == null) {
            return "redirect:/auth/login";
        }

        // 4) 이메일 -> 회사 DTO (null 방지)
        CompanyDTO companyDTO = companyService.getByUserEmail(admin.getEmail());
        if (companyDTO == null || companyDTO.getCompanyId() == null) {
            model.addAttribute("message", "회사 정보가 없습니다. 관리자에게 문의하세요.");
            return "error/500";
        }

        // 5) 페이징 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "accommodationId"));
        Page<AccommodationListDTO> result;
        if (status == null || status.isBlank()) {
            // 상태 미지정이면 기존 전체 호출
            result = accommodationService.findByCompanyId(companyDTO.getCompanyId(), pageable);
        } else {
            // 상태 지정 되면 회사+ 상태 조회
            result = accommodationService.findByCategoryIdAndStatus(companyDTO.getCompanyId(), status, pageable);
        }

        // 6) 모델 바인딩 — ★중요: 리스트는 getContent()로!
        model.addAttribute("page", result);                 // Page 객체 전체
        model.addAttribute("accommodations", result.getContent()); // 목록 아이템
        model.addAttribute("loginEmail", email);
        model.addAttribute("status", status);
        return "admin/accommodations/list";
    }

    /**
     * 상세 보기
     */
    @GetMapping("/detail/{id}")
    @PreAuthorize("@companyAccess.ownsAccommodation(#id, authentication)")
    public String detail(@PathVariable Long id, Model model) {
        AccommodationCreateRequestDTO acc = accommodationService.getById(id);
        if (acc == null) {
            return "redirect:/admin/accommodations/list";
        }

        // 표시용 이름들 보강(카테고리/지역/회사)
        String categoryName = null, mainRegionName = null, subRegionName = null, companyName = null;


        // 카테고리 이름
        if (acc.getCategoryId() != null) {
            AccommodationCategoryDTO cat = accommodationCategoryService.get(acc.getCategoryId());
            categoryName = (cat != null ? cat.getName() : null);
        }

        if (acc.getMainRegionId() != null) {
            RegionDTO main = regionService.get(acc.getMainRegionId());
            mainRegionName = (main != null ? main.getName() : null);
        }

        if (acc.getSubRegionId() != null) {
            RegionDTO sub = regionService.get(acc.getSubRegionId());
            subRegionName = (sub != null ? sub.getName() : null);
        }
        // 회사 이름
        if (acc.getCompanyId() != null) {
            CompanyDTO com = companyService.getById(acc.getCompanyId());
            companyName = (com != null ? com.getName() : null);
        }
        // 최소규정
        if (acc.getCancellationPolicyDetail() == null) {
            String policyJson = cancellationPolicyService.getMultilineByAccommodationId(id);
            acc.setCancellationPolicyDetail(policyJson);
        }
        // 태그 세팅 (안전하게)
        if (acc.getTags() == null || acc.getTags().isEmpty()) {
            // 1) 숙소-태그 매핑으로 먼저 조회
            List<AccommodationTagDTO> mapped = accommodationTagService.findByAccommodationId(acc.getAccommodationId());
            if (mapped != null && !mapped.isEmpty()) {
                acc.setTags(mapped);
            } else {
                // 2) 없으면 tagIds로 조회
                List<Long> tagIds = acc.getTagIds();
                if (tagIds != null && !tagIds.isEmpty()) {
                    List<AccommodationTagDTO> fromIds = accommodationTagService.findByIds(tagIds);
                    if (fromIds != null) {
                        acc.setTags(fromIds);
                    } else {
                        acc.setTags(Collections.emptyList());
                    }
                } else {
                    acc.setTags(Collections.emptyList());
                }
            }
        }
        // 4) 이미지 보강 (메인/서브 이미지 모두)
        if (acc.getImages() == null || acc.getImages().isEmpty()) {
            var imgs = accommodationImageService.getImages(acc.getAccommodationId());
            if (imgs != null && !imgs.isEmpty()) {
                // 정렬이 필요하다면 sortOrder 기준 정렬
                imgs.sort(Comparator.comparing(AccommodationImageDTO::getSortOrder, Comparator.nullsLast(Integer::compareTo)));
                acc.setImages(imgs);
            }
        }

        // 5) 취소규정 상세 보강 (문자열 JSON or 쉼표구분 텍스트)
        if (acc.getCancellationPolicyDetail() == null || acc.getCancellationPolicyDetail().isBlank()) {
            String policyJson = cancellationPolicyService.getMultilineByAccommodationId(id);
            acc.setCancellationPolicyDetail(policyJson);
        }

        // 6) 편의시설/취소규정 → 리스트 변환 (뷰에서 반복 출력) ← JSON 문자열을 List<String>으로 변환해서 모델에 담기
        List<String> amenitiesList = AmenitiesParser.toList(acc.getAmenities());
        List<String> policyList = AmenitiesParser.toList(acc.getCancellationPolicyDetail());


        String description = acc.getDescription();

        if (acc.getTags() == null || acc.getTags().isEmpty()) {
            List<AccommodationTagDTO> tags = accommodationTagService.findByAccommodationId(acc.getAccommodationId());
            if (tags != null && !tags.isEmpty()) {
                acc.setTags(tags);
            } else if (acc.getTagIds() != null && !acc.getTagIds().isEmpty()) {
                acc.setTags(accommodationTagService.findByIds(acc.getTagIds()));
            } else {
                acc.setTags(Collections.emptyList());
            }
        }


        model.addAttribute("acc", acc);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("mainRegionName", mainRegionName);
        model.addAttribute("subRegionName", subRegionName);
        model.addAttribute("companyName", companyName);

        model.addAttribute("amenitiesList", amenitiesList);
        model.addAttribute("policyList", policyList);
        model.addAttribute("description", description);

        model.addAttribute("tags", acc.getTags());
//        model.addAttribute("displayCheckIn", displayCheckIn);
//        model.addAttribute("displayCheckOut", displayCheckOut);


        return "admin/accommodations/detail";
    }

    /*
     *  총관리자에게 승인요청 (DRAFT/REJECTED → PENDING)
     *  과거에는 클라이언트가 to=APPROVED를 보내 총관리자 승인 없이 자가 승인이 가능했다(P1-1).
     *  회사 관리자는 PENDING 제출만 할 수 있고, 실제 승인/거절은 SUPER_ADMIN 전용 흐름에서만 이뤄진다.
     */
    @PostMapping("/{id}/request")
    @PreAuthorize("@companyAccess.ownsAccommodation(#id, authentication)")
    public String requestApproval(@PathVariable Long id) {
        accommodationService.changeStatus(id, AccommodationStatus.PENDING);
        return "redirect:/admin/accommodations/list?status=PENDING";
    }

    /*
     * (선택) 다건 승인요청
     */
    @PostMapping("/request")
    public String requestApprovalBulk(@RequestParam("ids") List<Long> ids,
                                      Authentication authentication) {
        Integer companyId = resourceAccessService.currentCompanyId(authentication);
        ids.stream()
                .filter(id -> resourceAccessService.ownsAccommodation(companyId, id))
                .forEach(id -> accommodationService.changeStatus(id, AccommodationStatus.PENDING));
        return "redirect:/admin/accommodations/list?status=PENDING";
    }

    // 지역(대)의 하위(소) 목록 JSON (select 연동)
    @GetMapping(value = "/regions/children", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> regionChildren(@RequestParam("parentId") Integer parentId) {
        return regionService.listSubRegions(parentId).stream()
                .map(r -> Map.<String, Object>of(
                        "id", r.getRegionId(),
                        "name", r.getName()
                ))
                .toList();
    }


    /*
     * 수정 폼 (GET)
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("@companyAccess.ownsAccommodation(#id, authentication)")
    public String editForm(@PathVariable Long id, Model model) {
        AccommodationCreateRequestDTO dto = accommodationService.getById(id);
        if (dto == null) return "redirect:/admin/accommodations/list";
        model.addAttribute("dto", dto);


        // dto amenities(json) -> list -> \n 문자열
        String amenitiesMultiline = AmenitiesParser.toMultiline(dto.getAmenities());

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
                .thumbnail(dto.getThumbnail())
                .description(dto.getDescription())
                .amenities(amenitiesMultiline)
                .cancellationPolicyDetail(null)
                .checkInTime(dto.getCheckInTime())
                .checkOutTime(dto.getCheckOutTime())
                .status(dto.getStatus())
                .minPrice(dto.getMinPrice())
                .images(dto.getImages() == null ? java.util.Collections.emptyList() : dto.getImages())
                .tagIds(dto.getTags() == null ? java.util.Collections.emptyList()
                        : dto.getTags().stream().map(t -> t.getTagId()).toList())
                .build();

        String policyMultiline = cancellationPolicyService.getMultilineByAccommodationId(id);
        form.setPolicyMultiline(policyMultiline);


        // 3) 셀렉트박스 데이터
        List<AccommodationCategoryDTO> categories = accommodationCategoryService.list();
        List<RegionDTO> mainRegions = regionService.listMainRegions();
        List<RegionDTO> subRegions = dto.getMainRegionId() == null ? Collections.emptyList() : regionService.listSubRegions(dto.getMainRegionId());

        // 전체 태그 목록 조회
        Map<String, List<AccommodationTagDTO>> tagsByCategory = accommodationTagService.findAllGroupedByCategory();

        // 현제 등록된 이미지 목록
        List<AccommodationImageDTO> images = accommodationImageService.getImages(id);

        // 4) 모델
        model.addAttribute("acc", dto);        // 상단 표시용(읽기)
        model.addAttribute("form", form);       // 폼 바인딩용
        model.addAttribute("images", images);
        model.addAttribute("categories", categories);
        model.addAttribute("mainRegions", mainRegions);
        model.addAttribute("subRegions", subRegions);
        model.addAttribute("tagsByCategory", tagsByCategory);
        return "admin/accommodations/edit"; // edit.html
    }

    /*
     * 수정 제출
     */
    @PostMapping("/edit/{id}")
    @PreAuthorize("@companyAccess.ownsAccommodation(#id, authentication)")
    public String editSubmit(@PathVariable Long id,
                             @ModelAttribute("form") AccommodationUpdateRequestDTO form,
                             BindingResult binding,
                             @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                             @RequestParam(value = "subImages", required = false) List<MultipartFile> subImages,
                             @RequestParam(value = "deleteThumbnail", defaultValue = "false") boolean deleteThumbnail,
                             @RequestParam(value = "deleteSubImageIds", required = false) List<Long> deleteSubImageIds,
                             RedirectAttributes ra,
                             Model model) {

        // 소유권은 @PreAuthorize 로 확인했다. 폼의 companyId 는 신뢰하지 않는다(서비스에서도 비운다)
        // (값을 넣어 보내면 AccommodationService.update 가 숙소를 다른 회사로 옮길 수 있었다).
        form.setCompanyId(null);

        if (binding.hasErrors()) {
            binding.getAllErrors().forEach(err -> log.error("bind err: {}", err));
            // 재표시용 셀렉트 데이터 다시 채우기
            model.addAttribute("acc", accommodationService.getById(id));
            model.addAttribute("images", accommodationImageService.getImages(id));
            model.addAttribute("categories", accommodationCategoryService.list());
            model.addAttribute("mainRegions", regionService.listMainRegions());
            model.addAttribute("subRegions", form.getMainRegionId() == null ? java.util.Collections.emptyList() : regionService.listSubRegions(form.getMainRegionId()));
            model.addAttribute("tagsByCategory", accommodationTagService.findAllGroupedByCategory());
            return "admin/accommodations/edit";
        }
        try {
            // 상태 검증 → 썸네일/이미지 → 필드 → 태그/환불 규정까지 한 트랜잭션 (실패 시 이번 요청에서 올린 파일 삭제)
            registrationService.edit(id, form, thumbnailFile, subImages, deleteThumbnail, deleteSubImageIds);
            ra.addFlashAttribute("success", "수정이 완료되었습니다.");
            return "redirect:/admin/accommodations/detail/{id}";
        } catch (Exception e) {
            log.error("editSubmit error", e);
            ra.addFlashAttribute("error", "수정 중 오류: " + e.getMessage());
            return "redirect:/admin/accommodations/edit/" + id;
        }
    }

    @GetMapping("/list/pending")
    public String listPending() {
        return "redirect:/admin/accommodations/list?status=PENDING";

    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("@companyAccess.ownsAccommodation(#id, authentication)")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            accommodationService.delete(id);
            ra.addAttribute("success", "숙소가 삭제되었습니다.");
        } catch (Exception e) {
            ra.addAttribute("error", "삭제 중 오류: " + e.getMessage());
        }
        return "redirect:/admin/accommodations/list";

    }
}
