package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.type.AccommodationStatus;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.serviceOwner.*;
import com.honeyrest.honeyrest_host.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller("ownerAccommodationController")
@RequiredArgsConstructor
@RequestMapping("/owner")
@Log4j2
public class AccommodationController {

    private final OAccommodationService accommodationService;
    private final OCompanyService companyService;
    private final OAccommodationCategory accommodationCategory;
    private final ORegionService regionService;
    private final FileStorage fileStorage;
    private final ORoomService roomService;
    /** 영구 삭제는 업체 관리자 화면과 같은 규칙(예약 이력 있으면 거부, 하위 데이터 순차 삭제)을 쓴다 */
    private final com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService adminAccommodationService;

    @GetMapping({"/accommodation/list", "/company/{companyId}/accommodations"})
    public String accommodations(@PathVariable(name = "companyId", required = false) Integer pathCompanyId,
                                 @RequestParam(name = "companyId", required = false) Integer paramCompanyId,
                                 @ModelAttribute PageRequestDTO pageRequestDTO,
                                 Model model) {
        // 목록 상단 업체 선택(?companyId=)이 무시되던 문제: 경로 변수와 쿼리 파라미터를 모두 받는다
        Integer companyId = pathCompanyId != null ? pathCompanyId : paramCompanyId;

        PageResponseDTO<AccommodationDTO> responseDTO =
                accommodationService.getAccommodationsWithPageable(companyId, pageRequestDTO);

        model.addAttribute("companyId", companyId != null ? companyId : 0);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("company", (companyId != null && companyId > 0) ? companyService.getCompany(companyId) : null);
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("accommodations", responseDTO.getDtoList());

        return "owner/accommodation/list";
    }

    @GetMapping("/accommodation/inActive/list")
    public String accommodationsInActiveList(@ModelAttribute PageRequestDTO pageRequestDTO,
                                             @RequestParam(required = false) Integer companyId,
                                             Model model) {

        PageResponseDTO<AccommodationDTO> responseDTO =
                accommodationService.getInActiveAccommodationsWithPageable(companyId, pageRequestDTO);

        model.addAttribute("companyId", companyId != null ? companyId : 0);
        model.addAttribute("companies", companyService.getAllCompanies());
        // 페이지 링크의 companyId=0(전체)으로 getCompany(0) 를 불러 404 가 나던 문제
        model.addAttribute("company", (companyId != null && companyId > 0) ? companyService.getCompany(companyId) : null);
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("accommodations", responseDTO.getDtoList());
        return "owner/accommodation/inActive";
    }


    @GetMapping("/accommodation/create")
    public String createAccommodation(@RequestParam(required = false) Integer companyId, Model model) {
        // 사이드바 "숙소 등록"은 companyId 없이 들어온다 (필수 파라미터라 500 이었다)
        model.addAttribute("companyId", companyId);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("categories", accommodationCategory.getAllAccommodationCategory());
        model.addAttribute("regions", regionService.getAllRegions());
        return "owner/accommodation/create";
    }

    @PostMapping("/accommodation/create")
    public String createAccommodation(@ModelAttribute AccommodationDTO accommodationDTO, RedirectAttributes ra) {
        try {
            // 대표 이미지는 선택 사항. 빈 파일을 그대로 올려 0바이트 썸네일(깨진 이미지)이 저장되던 문제
            MultipartFile file = accommodationDTO.getFile();
            String imageUrl = (file != null && !file.isEmpty()) ? fileStorage.upload(file, "accommodation") : null;
            accommodationDTO.setThumbnailUrl(imageUrl);

            Long accommodationId = accommodationService.registerAccommodation(accommodationDTO);

            if (imageUrl != null) {
                AccommodationImageDTO accommodationImageDTO = AccommodationImageDTO.builder()
                        .imageUrl(imageUrl)
                        .accommodationId(accommodationId)
                        .imageType("MAIN")
                        .sortOrder(0)
                        .build();
                accommodationService.registerAccommodationImage(accommodationImageDTO);
            }
            accommodationService.updateSubImages(accommodationId, accommodationDTO.getImages());
            ra.addFlashAttribute("success", "숙소를 등록했습니다.");
        } catch (Exception e) {
            // 과거에는 Model 에 넣고 redirect 해 오류가 사라졌다 (등록 실패를 알 수 없었다)
            log.warn("owner accommodation create failed", e);
            ra.addFlashAttribute("error", "숙소 등록 실패: " + e.getMessage());
            return "redirect:/owner/accommodation/create";
        }
        return AccommodationStatus.isActive(accommodationDTO.getStatus())
                ? "redirect:/owner/accommodation/list" : "redirect:/owner/accommodation/inActive/list";
    }

    @GetMapping("/accommodation/{accommodationId}/modify")
    public String modifyAccommodation(@PathVariable Long accommodationId, Model model) {
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationId));
        model.addAttribute("accommodationImages", accommodationService.getImagesByAccommodationIdOnlySub(accommodationId));
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("categories", accommodationCategory.getAllAccommodationCategory());
        model.addAttribute("regions", regionService.getAllRegions());
        return "owner/accommodation/modify";
    }

    @PostMapping("/accommodation/modify")
    public String modifyAccommodation(@ModelAttribute AccommodationDTO dto, RedirectAttributes ra) throws Exception {
        try {
            accommodationService.modifyAccommodation(dto);
            accommodationService.updateSubImages(dto.getAccommodationId(), dto.getImages());
            ra.addFlashAttribute("success", "숙소 정보를 수정했습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/owner/accommodation/" + dto.getAccommodationId() + "/modify";
        }
        return AccommodationStatus.isActive(dto.getStatus())
                ? "redirect:/owner/accommodation/list" : "redirect:/owner/accommodation/inActive/list";
    }

    /** 운영 중지(INACTIVE). 활성 목록의 버튼 — 데이터는 남는다. */
    @PostMapping("/accommodation/{accommodationId}/deactivate")
    public String deactivateAccommodation(@PathVariable Long accommodationId, RedirectAttributes ra) {
        accommodationService.removeAccommodation(accommodationId);
        ra.addFlashAttribute("success", "숙소를 비활성화했습니다.");
        return "redirect:/owner/accommodation/list";
    }

    /**
     * 영구 삭제. 과거에는 deleteById 로 지워 객실·이미지·예약 FK 때문에 항상 500 이 났다.
     * 예약 이력이 있으면 거부하고 비활성화를 안내한다.
     */
    @PostMapping("/accommodation/{accommodationId}/delete")
    public String deleteAccommodation(@PathVariable Long accommodationId, RedirectAttributes ra) {
        try {
            adminAccommodationService.delete(accommodationId);
            ra.addFlashAttribute("success", "숙소를 삭제했습니다.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage() + " 비활성화를 이용하세요.");
        }
        return "redirect:/owner/accommodation/inActive/list";
    }

    /**
     * 숙소 승인 (PENDING -> ACTIVE). 이 컨트롤러는 /owner/** 전체가 SecurityConfig에서
     * SUPER_ADMIN 전용으로 잠겨 있으므로, 회사 관리자는 이 경로에 도달할 수 없다(P1-1/P1-2).
     */
    @PostMapping("/accommodation/{accommodationId}/approve")
    public String approveAccommodation(@PathVariable Long accommodationId, RedirectAttributes ra) {
        try {
            accommodationService.approve(accommodationId);
            ra.addFlashAttribute("success", "숙소를 승인했습니다.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/owner/accommodation/list";
    }

    /** 숙소 거절 (PENDING -> REJECTED). */
    @PostMapping("/accommodation/{accommodationId}/reject")
    public String rejectAccommodation(@PathVariable Long accommodationId, RedirectAttributes ra) {
        try {
            accommodationService.reject(accommodationId);
            ra.addFlashAttribute("success", "숙소를 거절했습니다.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/owner/accommodation/inActive/list";
    }

    // 회사 검색 API (자동완성용)
    @GetMapping("/accommodation/companyId/search")
    @ResponseBody
    public List<AccommodationDTO> searchAccommodation(@RequestParam Integer companyId, @RequestParam String keyword) {
        return accommodationService.searchByNameContaining(companyId, keyword).stream().filter(a -> AccommodationStatus.isActive(a.getStatus())).toList();
    }

    // 회사 검색 API (자동완성용)
    @GetMapping("/accommodation/inActive/search")
    @ResponseBody
    public List<AccommodationDTO> searchInActiveAccommodation(@RequestParam Integer companyId, @RequestParam String keyword) {
        return accommodationService.searchByNameContaining(companyId, keyword).stream().filter(a -> !AccommodationStatus.isActive(a.getStatus())).toList();
    }

    // 선택한 회사의 숙소 목록 JSON
    @GetMapping("/accommodation/{accommodationId}/rooms/json")
    @ResponseBody
    public List<RoomDTO> getRoomsByAccommodation(@PathVariable Long accommodationId) {
        return roomService.getRoomsByAccommodationId(accommodationId);
    }
}
