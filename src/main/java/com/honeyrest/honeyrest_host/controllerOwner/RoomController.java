package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.config.FileUploadUtil;
import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
@Log4j2
public class RoomController {
    private final RoomService roomService;
    private final AccommodationService accommodationService;
    private final CompanyService companyService;
    private final PriceCalendarService priceCalendarService;
    private final ReservationService reservationService;
    private final FileUploadUtil fileUploadUtil;
    private final ReviewService reviewService;

    @GetMapping({"/room/list", "/accommodation/{accommodationId}/rooms"})
    public String rooms(
            @PathVariable(required = false) Long accommodationId,
            @ModelAttribute PageRequestDTO pageRequestDTO,
            Model model) {

        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        PageResponseDTO<RoomDTO> roomPage;

        if (accommodationId != null && accommodationId > 0) {
            roomPage = roomService.getRoomsByAccommodationIdWithPageable(accommodationId, pageRequestDTO);
            model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationId));
            model.addAttribute("accommodationId", accommodationId);
        } else {
            roomPage = roomService.getRoomsByAccommodationIdWithPageable(accommodationId ,pageRequestDTO);
            model.addAttribute("accommodationId", 0);
        }

        model.addAttribute("accommodations", accommodations);
        model.addAttribute("rooms", roomPage.getDtoList());
        model.addAttribute("pageResponse", roomPage);

        return "owner/room/list";
    }


    @GetMapping("/room/create")
    public String createRoom(@RequestParam Long accommodationId, Model model) {
        model.addAttribute("accommodationId", accommodationId);
        Long companyId = companyService.getCompanyIdByAccommodationId(accommodationId);
        model.addAttribute("accommodations", accommodationService.getAccommodationsByCompanyId(companyId));
        return "owner/room/create";
    }

    @PostMapping("/room/create")
    public String createRoom(@ModelAttribute RoomDTO roomDTO) throws Exception {
        Long roomId = roomService.registerRoom(roomDTO);

        List<MultipartFile> images = roomDTO.getImages();
        if (images != null && !images.isEmpty()) {
            int sortOrder = 1; // MAIN 이미지 다음부터
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String roomImageUrl = fileUploadUtil.upload(image,"room");
                    RoomImageDTO dto = RoomImageDTO.builder()
                            .roomId(roomId)
                            .imageUrl(roomImageUrl)
                            .sortOrder(sortOrder)
                            .build();
                    roomService.updateRoomImage(dto);
                }
            }
        }

        return "redirect:/owner/room/list";
    }

    @GetMapping("/room/{roomId}/modify")
    public String modifyRoom(@PathVariable Long roomId, Model model) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("room", roomService.getByRoomId(roomId));
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        return "owner/room/modify";
    }

    @PostMapping("/room/modify")
    public String modifyRoom(@ModelAttribute RoomDTO roomDTO) {
        roomService.modifyRoom(roomDTO);
        return "redirect:/owner/room/list";
    }

    @GetMapping("/room/search")
    @ResponseBody
    public List<RoomDTO> searchCompanies(@RequestParam Long accommodationId, @RequestParam String keyword) {
        return roomService.searchByNameContaining(accommodationId, keyword);
    }

    @GetMapping("/room/{roomId}/reviews/json")
    @ResponseBody
    public List<ReviewDTO> getReviewsByRoom(@PathVariable Long roomId) {
        return reviewService.getReviewsByRoomId(roomId);
    }

    @GetMapping("/room/{roomId}/reviews")
    public String reviews(@PathVariable Long roomId, Model model, PageRequestDTO pageRequestDTO) {
        model.addAttribute("room", roomService.getByRoomId(roomId));
        model.addAttribute("rooms", roomService.getRoomsByAccommodationId(accommodationService.getAccommodationIdByRoomId(roomId)));
        model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationService.getAccommodationIdByRoomId(roomId)));
        PageResponseDTO<ReviewDTO> reviews = reviewService.getReviewsWithPageable(roomId,  pageRequestDTO);
        model.addAttribute("pageResponse", reviews);
        model.addAttribute("reviews", reviews.getDtoList());
        return "owner/review/list";
    }
}
