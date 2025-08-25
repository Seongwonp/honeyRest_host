package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.CompanyDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationListDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationUpdateRequestDTO;
import com.honeyrest.honeyrest_host.entity.Company;
import com.honeyrest.honeyrest_host.entity.Region;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import com.honeyrest.honeyrest_host.repository.RegionRepository;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationCategoryRepository;
import com.honeyrest.honeyrest_host.service.AccommodationImageService;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Collections;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/accommodations")
public class AccommodationPageController {

    private final AccommodationService accommodationService;
    private final RegionRepository regionRepository;
    private final AccommodationCategoryRepository accommodationCategoryRepository;
    private final FileUploadUtil fileUploadUtil;
    private final AccommodationImageService accommodationImageService;
    private final CompanyService companyService;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;


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
     * 등록 제출 (승인상태는 서비스에서 PENDING 으로 고정됨)
     */
    @PostMapping("/add")
    public String addSubmit(@ModelAttribute("form") @Valid AccommodationCreateRequestDTO form) throws Exception {
        // 이미지 업로드
        fileUploadUtil.upload((MultipartFile) form.getImages(), "accommodation");
        accommodationService.create(form);
        return "redirect:/admin/accommodations/list";
    }

    /**
     * 내 명의(회사)의 숙소 목록 가져오기
     */
    @GetMapping("/list")
    public String list(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        // 1) 로그인 체크
        if (authentication == null || !authentication.isAuthenticated()
            || authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return "redirect:/admin/auth/login";
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
        var adminOpt = userRepository.findByEmail(email);
        if (adminOpt.isEmpty()) {
            return "redirect:/admin/auth/login";
        }
        var admin = adminOpt.get();

        // 4) 이메일 -> 회사 DTO (null 방지)
        CompanyDTO companyDTO = companyService.getByUserEmail(admin.getEmail());
        if (companyDTO == null || companyDTO.getCompanyId() == null) {
            model.addAttribute("message", "회사 정보가 없습니다. 관리자에게 문의하세요.");
            return "admin/common/error"; // 적절한 에러 페이지
        }

        // 5) 페이징 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "accommodationId"));
        Page<AccommodationListDTO> result = accommodationService.findByCompanyId(companyDTO.getCompanyId(), pageable);

        // 6) 모델 바인딩 — ★중요: 리스트는 getContent()로!
        model.addAttribute("page", result);                 // Page 객체 전체
        model.addAttribute("accommodations", result.getContent()); // 목록 아이템
        model.addAttribute("loginEmail", email);

        return "admin/accommodations/list";
    }

    /**
     * 상세 보기
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        // DTO에 name, categoryName, regionName, minPrice, status, address, description, amenities, images, tags ...을 채워서 반환
        AccommodationCreateRequestDTO acc = accommodationService.getById(id);
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


    /** 수정 폼 (GET)*/
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        AccommodationCreateRequestDTO dto = accommodationService.getById(id);

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
        model.addAttribute("form", form);// 폼 바인딩용
        model.addAttribute("images", accommodationImageService.getImages(id));
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


//    @GetMapping("/my")
//    public String myList(@AuthenticationPrincipal UserDetails me,
//                         @PageableDefault(size = 10, sort = "accommodationId", direction = Sort.Direction.DESC) Pageable pageable,
//                         Model model) {
//        String email = me.getUsername();
//        Long companyId = companyRepository.findByEmail(email).map(Company::getCompanyId)
//                .orElseThrow(() -> new UsernameNotFoundException("업체 관리자 이메일에 해당하는 회사가 없습니다."));
//        var page = accommodationService.findByCompanyId(companyId, pageable);
//
//        model.addAttribute("page", page);
//        model.addAttribute("list", page.getContent());
//        model.addAttribute("companyId", companyId);
//
//        return "admin/accommodations/list";

    }
