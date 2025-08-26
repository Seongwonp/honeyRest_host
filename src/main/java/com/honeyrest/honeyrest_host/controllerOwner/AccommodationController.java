package com.honeyrest.honeyrest_host.controllerOwner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.honeyrest.honeyrest_host.config.FileUploadUtil;
import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.AccommodationImageDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.service.AccommodationCategory;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final CompanyService companyService;
    private final AccommodationCategory accommodationCategory;
    private final RegionService regionService;
    private final FileUploadUtil fileUploadUtil;

    @GetMapping("/company/{companyId}/accommodations")
    public String accommodationsByCompany(@PathVariable Long companyId, Model model) {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        List<AccommodationDTO> accommodations;

        if (companyId != null) {
            accommodations = accommodationService.getAccommodationsByCompanyId(companyId);
            model.addAttribute("company", companyService.getCompany(companyId));
        } else {
            accommodations = accommodationService.getAllAccommodations();
        }
        model.addAttribute("companyId", companyId);
        model.addAttribute("companies", companies);
        model.addAttribute("accommodations", accommodations);

        return "owner/accommodation/list";
    }
    @GetMapping("/accommodation/list")
    public String accommodations(Model model) {
        model.addAttribute("companyId", 0);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
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
            MultipartFile[] images = accommodationDTO.getImages();
            if (images != null && images.length > 0) {
                int sortOrder = 1; // MAIN 이미지 다음부터
                for (MultipartFile img : images) {
                    if (!img.isEmpty()) {
                        String subImageUrl = fileUploadUtil.upload(img, "accommodation");
                        AccommodationImageDTO dto = AccommodationImageDTO.builder()
                                .imageUrl(subImageUrl)
                                .accommodationId(accommodationId)
                                .imageType("SUB") // 보조 이미지
                                .sortOrder(sortOrder++)
                                .build();
                        accommodationService.registerAccommodationImage(dto);
                    }
                }
            }

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/owner/accommodation/list"; // 성공 페이지

    }

    @GetMapping("/accommodation/{accommodationId}/modify")
    public String modifyAccommodation(@PathVariable Long accommodationId, Model model) {
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationId));
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("categories", accommodationCategory.getAllAccommodationCategory());
        model.addAttribute("regions", regionService.getAllRegions());
        return "owner/accommodation/modify";
    }

    @PostMapping("/accommodation/modify")
    public String modifyAccommodation(@ModelAttribute AccommodationDTO dto) throws JsonProcessingException {
        accommodationService.modifyAccommodation(dto);
        return "redirect:/owner/accommodation/list";
    }

    @PostMapping("/accommodation/{accommodationId}/delete")
    public String deleteAccommodation(@PathVariable Long accommodationId) {
        accommodationService.removeAccommodation(accommodationId);
        return "redirect:/owner/accommodation/list";
    }

}
