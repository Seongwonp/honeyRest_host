package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.config.FileUploadUtil;
import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
@Log4j2
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final CompanyService companyService;
    private final AccommodationCategory accommodationCategory;
    private final RegionService regionService;
    private final FileUploadUtil fileUploadUtil;
    private final RoomService roomService;

    @GetMapping({"/accommodation/list", "/company/{companyId}/accommodations"})
    public String accommodations(@PathVariable(required = false) Long companyId,
                                 @ModelAttribute PageRequestDTO pageRequestDTO,
                                 Model model) {

        // 기본 페이지 처리
        if (pageRequestDTO.getPage() <= 0) pageRequestDTO.setPage(1);
        if (pageRequestDTO.getSize() <= 0) pageRequestDTO.setSize(10);

        PageResponseDTO<AccommodationDTO> responseDTO =
                accommodationService.getAccommodationsWithPageable(companyId, pageRequestDTO);

        model.addAttribute("companyId", companyId != null ? companyId : 0);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("company", companyId != null ? companyService.getCompany(companyId) : null);
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("accommodations", responseDTO.getDtoList());

        return "owner/accommodation/list";
    }

    @GetMapping("/accommodation/inActive/list")
    public String accommodationsInActiveList(@ModelAttribute PageRequestDTO pageRequestDTO,
                                             @RequestParam(required = false) Long companyId,
                                             Model model) {

        PageResponseDTO<AccommodationDTO> responseDTO =
                accommodationService.getInActiveAccommodationsWithPageable(companyId, pageRequestDTO);

        model.addAttribute("companyId", companyId != null ? companyId : 0);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("company", companyId != null ? companyService.getCompany(companyId) : null);
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("accommodations", responseDTO.getDtoList());
        model.addAttribute("inActive", 1);
        return "owner/accommodation/list";
    }


    @GetMapping("/accommodation/create")
    public String createAccommodation(@RequestParam Long companyId, Model model) {
        model.addAttribute("companyId", companyId);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("categories", accommodationCategory.getAllAccommodationCategory());
        model.addAttribute("regions", regionService.getAllRegions());
        return "owner/accommodation/create";
    }

    @PostMapping("/accommodation/create")
    public String createAccommodation(@ModelAttribute AccommodationDTO accommodationDTO, Model model) {
        try {
            // Firebase 업로드
            MultipartFile file = accommodationDTO.getFile();
            String imageUrl = fileUploadUtil.upload(file, "accommodation");
            accommodationDTO.setThumbnailUrl(imageUrl);

            Long accommodationId = accommodationService.registerAccommodation(accommodationDTO);

            AccommodationImageDTO accommodationImageDTO = AccommodationImageDTO.builder()
                    .imageUrl(imageUrl)
                    .accommodationId(accommodationId)
                    .imageType("MAIN")
                    .sortOrder(0)
                    .build();
            accommodationService.registerAccommodationImage(accommodationImageDTO);
            List<MultipartFile> images = accommodationDTO.getImages();
            accommodationService.updateSubImages(accommodationId, images);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/owner/accommodation/list"; // 성공 페이지

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
    public String modifyAccommodation(@ModelAttribute AccommodationDTO dto) throws Exception {
        accommodationService.modifyAccommodation(dto);
        Long accommodationId = dto.getAccommodationId();
        List<MultipartFile> images = dto.getImages();
        accommodationService.updateSubImages(accommodationId, images);
        return "redirect:/owner/accommodation/list";
    }

    @PostMapping("/accommodation/{accommodationId}/delete")
    public String deleteAccommodation(@PathVariable Long accommodationId) {
        accommodationService.removeAccommodation(accommodationId);
        return "redirect:/owner/accommodation/list";
    }

    // 회사 검색 API (자동완성용)
    @GetMapping("/accommodation/search")
    @ResponseBody
    public List<AccommodationDTO> searchCompanies(@RequestParam Long companyId, @RequestParam String keyword) {
        return accommodationService.searchByNameContaining(companyId, keyword);
    }

    // 선택한 회사의 숙소 목록 JSON
    @GetMapping("/accommodation/{accommodationId}/rooms/json")
    @ResponseBody
    public List<RoomDTO> getRoomsByAccommodation(@PathVariable Long accommodationId) {
        return roomService.getRoomsByAccommodationId(accommodationId);
    }
}
