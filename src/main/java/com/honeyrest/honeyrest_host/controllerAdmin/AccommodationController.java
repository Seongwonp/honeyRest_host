package com.honeyrest.honeyrest_host.controllerAdmin;



import com.honeyrest.honeyrest_host.dtoAdmin.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.RegionDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.*;
import com.honeyrest.honeyrest_host.repositoryAdmin.RegionRepository;
import com.honeyrest.honeyrest_host.serviceAdmin.CancellationPolicyService;
import com.honeyrest.honeyrest_host.serviceAdmin.RegionService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationCategoryService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationImageService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyService;
import com.honeyrest.honeyrest_host.serviceAdmin.UserService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationTagService;
import com.honeyrest.honeyrest_host.utilAdmin.AmenitiesParser;
import com.honeyrest.honeyrest_host.utilAdmin.FileUploadUtil;
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
    private final FileUploadUtil fileUploadUtil;
    private final UserService userService;
    private final AccommodationTagService accommodationTagService;
    private final CancellationPolicyService cancellationPolicyService;


    private final RegionRepository regionRepository;
    private final AccommodationCategoryService accommodationCategoryService;
    private final RegionService regionService;


    /*
     * 등록 화면
     */
    @GetMapping("/add")
    public String addPage(Model model) {
        AccommodationCreateRequestDTO form = new AccommodationCreateRequestDTO();

        form.setTagIds(Collections.emptyList());


        model.addAttribute("mainRegions", regionRepository.findByLevel(1));
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
                            RedirectAttributes ra) {
        if (binding.hasErrors()) {
            binding.getAllErrors().forEach(err -> log.warn("bind err: {}", err));
            model.addAttribute("mainRegions", regionRepository.findByLevel(1));
            model.addAttribute("tagsByCategory", accommodationTagService.findAllGroupedByCategory());
            return "admin/accommodations/add";
        }

        try {

            // (1) 대표 썸네일
            if (form.getFile() != null && !form.getFile().isEmpty()) {
                String url = fileUploadUtil.upload(form.getFile(), "accommodations");
                form.setThumbnail(url);
            }

            // (2) 편의시설 JSON 정규화
            form.setAmenities(AmenitiesParser.normalizeToJson(form.getAmenities()));

            // (3) 숙소 저장
            AccommodationCreateRequestDTO saved = accommodationService.create(form);
            Long accId = saved.getAccommodationId();

            // 태그 매핑
//            List<Long> tagIds = (form.getTagIds() == null) ? Collections.emptyList() : form.getTagIds();
//            accommodationTagService.replaceMapping(accId, tagIds);
            if(form.getTagIds() == null) form.setTagIds(Collections.emptyList());

            // 환불 정책
            cancellationPolicyService.saveOrUpdate(accId, form.getCancellationPolicyDetail());

            // (4) 메인 썸네일 → image 테이블
            if (form.getThumbnail() != null && !form.getThumbnail().isBlank()) {
                accommodationImageService.upsertMainThumbnail(
                        accId,
                        AccommodationImageDTO.builder()
                                .imageUrl(form.getThumbnail())
                                .imageType("MAIN")
                                .sortOrder(0)
                                .build()
                );
                accommodationImageService.updateThumbnailUrl(accId, form.getThumbnail());
            }

            // (5) 서브 이미지들 업로드 후 저장
            if (subImages != null && !subImages.isEmpty()) {
                int order = 1;
                for (MultipartFile f : subImages) {
                    if (f.isEmpty()) continue;
                    String url = fileUploadUtil.upload(f, "accommodations/" + accId + "/images");
                    accommodationImageService.saveOrUpload(
                            accId,
                            AccommodationImageDTO.builder()
                                    .file(f)
                                    .imageUrl(url)
                                    .imageType("SUB")
                                    .sortOrder(order++)
                                    .build()
                    );
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
            @RequestParam(defaultValue = "12") int size,
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
    public List<Map<String, Object>> regionChildren(@RequestParam("parentId") Integer parentId) {
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
    public String editSubmit(@PathVariable Long id,
                             @ModelAttribute("form") AccommodationUpdateRequestDTO form,
                             BindingResult binding,
                             @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                             @RequestParam(value = "subImages", required = false) List<MultipartFile> subImages,
                             // 삭제 토글/리스트 (UI에서 hidden으로 전달)
                             @RequestParam(value = "deleteThumbnail", defaultValue = "false") boolean deleteThumbnail,
                             @RequestParam(value = "deleteSubImageIds", required = false) List<Long> deleteSubImageIds,
                             RedirectAttributes ra,
                             Model model) {

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
            // 1) amenities 정규화
            form.setAmenities(AmenitiesParser.normalizeToJson(form.getAmenities()));

            String policyJson = AmenitiesParser.normalizeToJson(form.getPolicyMultiline());
            form.setPolicyMultiline(policyJson);

            // 2) 썸네일 처리
            String newMainUrl = null;

            if (deleteThumbnail) {
                // ui 에서 삭제 토글한 경우
                accommodationImageService.delete(id);
                form.setThumbnail(null);
            }
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                // 파일 업로드한 경우 → 새 URL을 DTO에 넣어 엔티티 필드에도 반영되게
                newMainUrl = fileUploadUtil.upload(thumbnailFile, "accommodations");
                form.setThumbnail(newMainUrl);
            } else if (form.getThumbnail() != null && !form.getThumbnail().isBlank()) {
                // 파일 업로드 없이 URL만 입력/유지하는 경우
                newMainUrl = form.getThumbnail();
            } // else: 아무 것도 없으면 null 유지 → 썸네일 미변경

            // 3) 스칼라/연관 필드 업데이트(썸네일 포함)
            accommodationService.update(id, form);

            // 4) 메인 이미지(이미지 테이블) upsert (썸네일 URL이 최종 있으면만)
            if (newMainUrl != null && !newMainUrl.isBlank()) {
                accommodationImageService.upsertMainThumbnail(
                        id,
                        AccommodationImageDTO.builder()
                                .imageType("MAIN")
                                .sortOrder(0)
                                .imageUrl(newMainUrl)
                                .build()
                );
            }

            // 기존 sub 이미지 삭제(있다면)
            if (deleteSubImageIds != null && !deleteSubImageIds.isEmpty()) {
                accommodationImageService.deleteSubImages(deleteSubImageIds);
            }

            // 5) SUB 이미지 추가
            if (subImages != null && !subImages.isEmpty()) {
                int sortSeed = 1; // MAIN=0 다음
                for (MultipartFile file : subImages) {
                    if (file == null || file.isEmpty()) continue;
                    String url = fileUploadUtil.upload(file, "accommodations/" + id + "/images");
                    accommodationImageService.saveOrUpload(
                            id,
                            AccommodationImageDTO.builder()
                                    .imageUrl(url)
                                    .imageType("SUB")
                                    .sortOrder(sortSeed++)
                                    .build()
                    );

                }

            }

            List<Long> tagIds = (form.getTagIds() == null) ? Collections.emptyList() : form.getTagIds();
            accommodationTagService.replaceMapping(id, tagIds);
            // 환불 규정 저장
            cancellationPolicyService.saveOrUpdate(id, form.getPolicyMultiline());

            ra.addFlashAttribute("success", "수정이 완료되었습니다.");
            return "redirect:/admin/accommodations/detail/{id}"; // 또는 detail/{id}로

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
