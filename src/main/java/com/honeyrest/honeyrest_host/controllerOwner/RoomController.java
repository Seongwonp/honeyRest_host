package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.serviceOwner.*;
import com.honeyrest.honeyrest_host.utilAdmin.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Controller("ownerRoomController")
@RequestMapping("/owner")
@RequiredArgsConstructor
@Log4j2
public class RoomController {
    private final ORoomService roomService;
    private final OAccommodationService accommodationService;
    private final OCompanyService companyService;
    private final OPriceCalendarService priceCalendarService;
    private final OReservationService reservationService;
    private final FileUploadUtil fileUploadUtil;
    private final OReviewService reviewService;

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
        List<AccommodationImageDTO> subImages = accommodationService.getImagesByAccommodationIdOnlySub(accommodationId);
        List<RoomImageDTO> roomImageDTOS = roomService.getAllImages();
        model.addAttribute("images", roomImageDTOS);
        model.addAttribute("subImages", subImages);
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("rooms", roomPage.getDtoList());
        model.addAttribute("pageResponse", roomPage);

        return "owner/room/list";
    }

    @GetMapping("/room/inActive/list")
    public String inActiveList(@ModelAttribute PageRequestDTO pageRequestDTO,
                               @RequestParam(required = false) Long companyId,
                               Model model) {

        return "owner/room/inActive";
    }

    @GetMapping("/room/create")
    public String createRoom(@RequestParam Long accommodationId, Model model) {
        model.addAttribute("accommodationId", accommodationId);
        if (accommodationId != 0) {
            Long companyId = companyService.getCompanyIdByAccommodationId(accommodationId);
            model.addAttribute("accommodations", accommodationService.getAccommodationsByCompanyId(companyId));
        } else {
            model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        }
        return "owner/room/create";
    }

    @PostMapping("/room/create")
    public String createRoom(@ModelAttribute RoomDTO roomDTO) throws Exception {
        Long roomId = roomService.registerRoom(roomDTO);

        String mainImage = fileUploadUtil.upload(roomDTO.getFile(),"rooms");
        RoomImageDTO roomImageDTO = RoomImageDTO.builder()
                .roomId(roomId)
                .imageUrl(mainImage)
                .sortOrder(0)
                .build();
        roomService.updateRoomImage(roomImageDTO);

        List<MultipartFile> images = roomDTO.getImages();
        if (images != null && !images.isEmpty()) {
            int sortOrder = 1; // MAIN 이미지 다음부터
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String roomImageUrl = fileUploadUtil.upload(image,"rooms");
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
        model.addAttribute("images", roomService.getImagesByRoomId(roomId));
        return "owner/room/modify";
    }

    @PostMapping("/room/modify")
    public String modifyRoom(@ModelAttribute RoomDTO roomDTO) throws Exception {
        roomService.modifyRoom(roomDTO);
        Long roomId = roomDTO.getRoomId();
        List<MultipartFile> images = roomDTO.getImages();
        roomService.modifyRoomImage(roomId, images);
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
