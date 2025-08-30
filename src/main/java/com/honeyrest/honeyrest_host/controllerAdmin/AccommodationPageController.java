package com.honeyrest.honeyrest_host.controllerAdmin;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dto.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dto.CompanyDTO;
import com.honeyrest.honeyrest_host.dto.RegionDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.*;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.AccommodationImage;
import com.honeyrest.honeyrest_host.entity.AccommodationTag;
import com.honeyrest.honeyrest_host.entity.Region;
import com.honeyrest.honeyrest_host.repository.RegionRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationCategoryRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationTagRepository;
import com.honeyrest.honeyrest_host.service.RegionService;
import com.honeyrest.honeyrest_host.service.accommodation.AccommodationCategoryService;
import com.honeyrest.honeyrest_host.service.accommodation.AccommodationImageService;
import com.honeyrest.honeyrest_host.service.accommodation.AccommodationService;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.service.UserService;
import com.honeyrest.honeyrest_host.service.accommodation.AccommodationTagService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Arrays;
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
    private final AccommodationImageService accommodationImageService;
    private final CompanyService companyService;
    private final FileUploadUtil fileUploadUtil;
    private final UserService userService;
    private final AccommodationTagService accommodationTagService;
    private final ObjectMapper objectMapper;


    private final RegionRepository regionRepository;
    private final AccommodationCategoryService accommodationCategoryService;
    private final RegionService regionService;

    /*
     * 등록 화면
     */
    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("mainRegions", regionRepository.findByLevel(1));
        model.addAttribute("form", new AccommodationCreateRequestDTO());
        model.addAttribute("tagsByCategory", accommodationTagService.findAllGroupedByCategory());
        return "admin/accommodations/add";
    }

    /*
     * 등록 제출 (승인상태는 서비스에서 PENDING 으로 고정됨)
     */
    @PostMapping("/add")
    public String addSubmit(@ModelAttribute("form") @Valid AccommodationCreateRequestDTO form, BindingResult binding,
                            Model model,
                            RedirectAttributes ra) {
        if (binding.hasErrors()) {
            binding.getAllErrors().forEach(err -> log.warn("bind err: {}", err));
            accommodationTagService.findAll();

            // 화면 다시 그릴 때 필요한 데이터 다시 주입
            model.addAttribute("mainRegions", regionRepository.findByLevel(1));
            model.addAttribute("tagsByCategory", accommodationTagService.findAllGroupedByCategory());
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

            // 2) 숙소 등록 json 배열 문자열로 정규화
            form.setAmenities(parseAmenitiesToJson(form.getAmenities()));
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
                accommodationImageService.updateThumbnailUrl(accId, form.getThumbnailUrl());
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
            log.error("add error", e);
            ra.addFlashAttribute("error", "등록 중 오류: " + e.getMessage());
            ra.addAttribute("form", form);
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
        AdminLoginRequestDTO admin = userService.getUserByEmail(email);
        if (admin == null) {
            return "redirect:/admin/auth/login";
        }

        // 4) 이메일 -> 회사 DTO (null 방지)
        CompanyDTO companyDTO = companyService.getByUserEmail(admin.getEmail());
        if (companyDTO == null || companyDTO.getCompanyId() == null) {
            model.addAttribute("message", "회사 정보가 없습니다. 관리자에게 문의하세요.");
            return "admin/common/error"; // 적절한 에러 페이지
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
    public String detail(@PathVariable Long id, Model model) {
        // DTO에 name, categoryName, regionName, minPrice, status, address, description, amenities, images, tags ...을 채워서 반환
        AccommodationCreateRequestDTO acc = accommodationService.getById(id);


        // 카테고리 이름
        String categoryName = null;
        if (acc.getCategoryId() != null) {
            AccommodationCategoryDTO cat = accommodationCategoryService.get(acc.getCategoryId());
            categoryName = (cat != null ? cat.getName() : null);
        }

        String mainRegionName = null;
        if (acc.getMainRegionId() != null) {
            RegionDTO main = regionService.get(acc.getMainRegionId());
            mainRegionName = (main != null ? main.getName() : null);
        }

        String subRegionName = null;
        if (acc.getSubRegionId() != null) {
            RegionDTO sub = regionService.get(acc.getSubRegionId());
            subRegionName = (sub != null ? sub.getName() : null);
        }
        // 회사 이름
        String companyName = null;
        if (acc.getCompanyId() != null) {
            CompanyDTO com = companyService.getById(acc.getCompanyId());
            companyName = (com != null ? com.getName() : null);
        }

        // ← JSON 문자열을 List<String>으로 변환해서 모델에 담기
        List<String> amenitiesList = parseAmenitiesToList(acc.getAmenities());


        model.addAttribute("acc", acc);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("mainRegionName", mainRegionName);
        model.addAttribute("subRegionName", subRegionName);
        model.addAttribute("companyName", companyName);
        model.addAttribute("amenitiesList", amenitiesList);

        return "admin/accommodations/detail";
    }

    /*
     *  총관리자에게 승인요청 (DRAFT/REJECTED → PENDING)
     */
    @PostMapping("/{id}/request")
    public String requestApproval(@PathVariable Long id,
                                  @RequestParam(required = false, defaultValue = "PENDING") String to) {
        accommodationService.changeStatus(id, to); // "PENDING"
        return "redirect:/admin/accommodations/list?status=PENDING";
    }

    /*
     * (선택) 다건 승인요청
     */
    @PostMapping("/request")
    public String requestApprovalBulk(@RequestParam("ids") List<Long> ids) {
        ids.forEach(i -> accommodationService.changeStatus(i, "PENDING"));
        return "redirect:/admin/accommodations/list?status=PENDING";
    }

    // 지역(대)의 하위(소) 목록 JSON (select 연동)
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


    /*
     * 수정 폼 (GET)
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        // 기본 dto 조회
        AccommodationCreateRequestDTO dto = accommodationService.getById(id);
        // dto amenities(json) -> list -> \n 문자열
        List<String> amenListForForm = parseAmenitiesToList(dto.getAmenities());
        String amenitiesMultiline = amenitiesListToMultiline(amenListForForm);

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
                .amenities(amenitiesMultiline)
                .checkInTime(dto.getCheckInTime())
                .checkOutTime(dto.getCheckOutTime())
                .status(dto.getStatus())
                .minPrice(dto.getMinPrice())
                .images(dto.getImages() == null ? java.util.Collections.emptyList() : dto.getImages())
                .tagIds(dto.getTags() == null ? java.util.Collections.emptyList()
                        : dto.getTags().stream().map(t -> t.getTagId()).toList())
                .build();

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
        log.info("checkin {} : ", form.getCheckInTime());
        log.info("checkout {} : ", form.getCheckOutTime());


        return "admin/accommodations/edit"; // edit.html
    }

    /*
     * 수정 제출
     */
    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id,
                             @ModelAttribute("form") AccommodationUpdateRequestDTO form,
                             @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
                             @RequestParam(value = "subImages", required = false) List<MultipartFile> subImages,
                             RedirectAttributes ra) {
        try {
            // 1) 텍스트 업데이트
            form.setAmenities(parseAmenitiesToJson(form.getAmenities()));

            // 2) 썸네일 파일이 올라온 경우만 업로드+DB 반영
            if (thumbnail != null && !thumbnail.isEmpty()) {
                // 파일 업로드 한 경우
                String newMainUrl = null;
                if (thumbnail != null && !thumbnail.isEmpty()) {
                    String uploadedUrl = fileUploadUtil.upload(thumbnail, "accommodation");
                    newMainUrl = uploadedUrl;
                    form.setThumbnailUrl(newMainUrl); // 서비스 update가 썸네일도 갱신 가능하도록 하기 위함.
                } else if (form.getThumbnailUrl() != null && !form.getThumbnailUrl().isEmpty()) {
                    newMainUrl = form.getThumbnailUrl(); // 파일 없이 기존/ 새 url 유지 하기 위함
                }
                accommodationService.update(id, form);

                // main 이미지 upsert + 숙소 썸네일 url 동기화
                if (newMainUrl != null) {
                    AccommodationImageDTO mainDto = AccommodationImageDTO.builder()
                            .imageType("MAIN")
                            .sortOrder(0)
                            .imageUrl(newMainUrl)
                            .build();

                    AccommodationImageDTO savedMain = accommodationImageService.upsertMainThumbnail(id, mainDto);
                    // 숙소 테이블의 thumbnail.url 을 메인과 동일하게 맞추기 위함(썸네일=메인)
                    accommodationImageService.updateThumbnailUrl(id, savedMain.getImageUrl());
                }

                // 4) SUB 이미지들 추가/수정
                if (subImages != null && !subImages.isEmpty()) {
                    int sortSeed = 1; // MAIN=0 이후부터
                    for (MultipartFile file : subImages) {
                        if (file == null || file.isEmpty()) continue;
                        accommodationImageService.saveOrUpload(id, AccommodationImageDTO.builder()
                                .imageUrl("SUB").sortOrder(sortSeed++).file(file).build());
                    }
                }
                ra.addFlashAttribute("success", "수정이 완료되었습니다.");
                return "redirect:/admin/accommodations/list";
            }

            } catch(Exception e){
                log.error("==============editSubmit error", e);   // ★ stacktrace 확인용
                ra.addFlashAttribute("error", "이미지 처리 중 오류: " + e.getMessage());
                return "redirect:/admin/accommodations/edit/" + id;
            }
        return "";
    }


    @GetMapping("/list/pending")
    public String listPending() {
        return "redirect:/admin/accommodations/list?status=PENDING";

    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            accommodationService.delete(id);
            ra.addAttribute("success", "숙소가 삭제되었습니다.");
        } catch (Exception e) {
            ra.addAttribute("error", "삭제 중 오류: " + e.getMessage());
        }
        return "redirect:/admin/accommodations/list";
    }


    //json문자열을 List로 변환
    private List<String> parseAmenitiesToList(String jsonInput) {
        if (jsonInput == null || jsonInput.isBlank()) return Collections.emptyList();

        try {
            // JSON 배열 문자열을 List<String>으로 역직렬화
            return objectMapper.readValue
                    (jsonInput, new TypeReference<List<String>>() {
                    });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList(); // 실패 시 빈 리스트 반환
        }
    }

    //List를 문자열로 변환(textarea)
    private String amenitiesListToMultiline(List<String> list) {
        return String.join("\n ", list);
    }


    // 줄바꿈/쉼표 섞인 입력 -> josn 문자열(저장용)
    private String parseAmenitiesToJson(String input) {
        if (input == null || input.isBlank()) return "[]";
        List<String> amenitiesList = Arrays.stream(input.split("[,\\r?\\n]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        try {
            return objectMapper.writeValueAsString(amenitiesList);
        } catch (JsonProcessingException e) {
            return "[]"; // 실패 시 빈 배열 반환
        }
    }
}
