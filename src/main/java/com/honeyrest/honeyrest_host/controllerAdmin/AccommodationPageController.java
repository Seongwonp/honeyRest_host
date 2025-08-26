package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.CompanyDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationImageDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationListDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationUpdateRequestDTO;
import com.honeyrest.honeyrest_host.entity.AccommodationTag;
import com.honeyrest.honeyrest_host.entity.Region;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import com.honeyrest.honeyrest_host.repository.RegionRepository;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationCategoryRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationTagRepository;
import com.honeyrest.honeyrest_host.service.AccommodationImageService;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/accommodations")
public class AccommodationPageController {

    private final AccommodationService accommodationService;
    private final AccommodationCategoryRepository accommodationCategoryRepository;
    private final AccommodationImageService accommodationImageService;
    private final AccommodationTagRepository accommodationTagRepository;
    private final RegionRepository regionRepository;
    private final CompanyService companyService;
    private final UserRepository userRepository;
    private final FileUploadUtil fileUploadUtil;


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
    public String addSubmit(@ModelAttribute("form") @Valid AccommodationCreateRequestDTO form, BindingResult binding,
                            Model model,
                            RedirectAttributes ra) {
        if (binding.hasErrors()) {
            binding.getAllErrors().forEach(err -> log.warn("bind err: {}", err));
            // 화면 다시 그릴 때 필요한 데이터 다시 주입
            model.addAttribute("mainRegions", regionRepository.findByLevel(1));
            return "admin/accommodations/add"; // redirect 대신 포워드
        }

        try {
            // 1) 대표 썸네일 업로드 (파일이 올라온 경우)
            if (form.getFile() != null && !form.getFile().isEmpty()) {
                log.info("aaaaaaaaaaaaaaaaaaa");
                String url = fileUploadUtil.upload(form.getFile(), "accommodation");
                form.setThumbnailUrl(url); // DB에 저장될 썸네일 URL
                log.info("bbbbbbbbbbbbbbbbbb");
            }

            // 2) 숙소 등록 (서비스에서 상태 PENDING 처리)
            form.setAmenities(csvToJsonArray(form.getAmenities()));
            AccommodationCreateRequestDTO saved = accommodationService.create(form);
            Long accId = saved.getAccommodationId(); // create가 id를 리턴하도록 하세요.

            log.info("ccccccccccccccccccc");

            // (3) 메인 썸네일을 이미지 테이블에 upsert
            if (form.getThumbnailUrl() != null && !form.getThumbnailUrl().isBlank()) {
                accommodationImageService.upsertMainThumbnail(
                        accId,
                        AccommodationImageDTO.builder()
                                .imageUrl(form.getThumbnailUrl())  // 파일 없이 URL만 들어와도 됨
                                .imageType("MAIN")
                                .sortOrder(0)
                                .build()

                );
                log.info("dddddddddddddddddd");
            }

            // (4) 추가 이미지들 업로드 → 이미지 테이블 저장
            if (form.getImages() != null && !form.getImages().isEmpty()) {
                int order = 1;
                for (AccommodationImageDTO imgDto : form.getImages()) {
                    imgDto.setAccommodationId(accId);
                    if (imgDto.getSortOrder() == null) imgDto.setSortOrder(order++);
                    if (imgDto.getImageType() == null || imgDto.getImageType().isBlank()) {
                        imgDto.setImageType("SUB");
                    }
                    accommodationImageService.upsertMainThumbnail(accId, imgDto);
                }
            }


            ra.addFlashAttribute("success", "숙소 등록이 요청되었습니다.");
            return "redirect:/admin/accommodations/list?status=PENDING";

        } catch (Exception e) {
            log.error("add error",e);
            ra.addFlashAttribute("error", "등록 중 오류: " + e.getMessage());
            ra.addAttribute("form", form);
            return "redirect:/admin/accommodations/add";
        }

    }

    /**
     * 내 명의(회사)의 숙소 목록 가져오기
     */
    @GetMapping("/list")
    public String list(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
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
        Page<AccommodationListDTO> result;
        if(status == null || status.isBlank()) {
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


    /**
     * 수정 폼 (GET)
     */
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
        // 전체 태그 목록 조회
        var all = accommodationTagRepository.findAll(Sort.by("category","name").ascending());
        var tagsByCategory = all.stream().collect(java.util.stream.Collectors.groupingBy(
                AccommodationTag::getCategory,
                java.util.TreeMap::new,
                java.util.stream.Collectors.toList()
                ));

        // 4) 모델
        model.addAttribute("acc", dto);        // 상단 표시용(읽기)
        model.addAttribute("form", form);// 폼 바인딩용
        model.addAttribute("images", accommodationImageService.getImages(id));
        model.addAttribute("categories", categories);
        model.addAttribute("mainRegions", mainRegions);
        model.addAttribute("subRegions", subRegions);
        model.addAttribute("tagsByCategory", tagsByCategory);


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

        ra.addAttribute("updated", true);
        return "redirect:/admin/accommodations/list";
    }


    @GetMapping("/list/pending")
    public String listPending() {
        return "redirect:/admin/accommodations/list?status=PENDING";

    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            accommodationService.delete(id);
            ra.addAttribute("success","숙소가 삭제되었습니다.");
        } catch (Exception e) {
            ra.addAttribute("error","삭제 중 오류: " + e.getMessage());
        }
        return "redirect:/admin/accommodations/list";
    }


    // CSV("와이파이, 주차") -> ["와이파이","주차"] -> string 으로 변환
    private String csvToJsonArray(String csv) {
        try {
            if (csv == null) return "[]";
            String s = csv.trim();
            if (s.isEmpty()) return "[]";

            // 이미 JSON 배열이면 유효성만 확인하고 그대로
            if (s.startsWith("[") && s.endsWith("]")) {
                new com.fasterxml.jackson.databind.ObjectMapper().readTree(s);
                return s;
            }

            java.util.List<String> list = java.util.Arrays.stream(s.split(","))
                    .map(String::trim)
                    .filter(x -> !x.isEmpty())
                    .toList();

            return list.isEmpty()
                    ? "[]"
                    : new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

}
