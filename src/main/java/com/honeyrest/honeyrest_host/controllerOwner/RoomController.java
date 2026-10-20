package com.honeyrest.honeyrest_host.controllerOwner;

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
    private final FileStorage fileStorage;
    private final OReviewService reviewService;

    @GetMapping({"/room/list", "/accommodation/{accommodationId}/rooms"})
    public String rooms(
            @PathVariable(name = "accommodationId", required = false) Long pathAccommodationId,
            @RequestParam(name = "accommodationId", required = false) Long paramAccommodationId,
            @ModelAttribute PageRequestDTO pageRequestDTO,
            Model model) {
        // 경로 변수와 쿼리 파라미터(?accommodationId=)를 모두 받는다
        Long accommodationId = pathAccommodationId != null ? pathAccommodationId : paramAccommodationId;
        fillRoomList(accommodationId, true, pageRequestDTO, model);
        return "owner/room/list";
    }

    /** 비활성(판매 중지) 객실 목록 */
    @GetMapping("/room/inActive/list")
    public String inActiveList(@ModelAttribute PageRequestDTO pageRequestDTO,
                               @RequestParam(required = false) Long accommodationId,
                               Model model) {
        fillRoomList(accommodationId, false, pageRequestDTO, model);
        return "owner/room/inActive";
    }

    private void fillRoomList(Long accommodationId, boolean active, PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<RoomDTO> roomPage = roomService.getRoomsByStatusWithPageable(accommodationId, active, pageRequestDTO);
        if (accommodationId != null && accommodationId > 0) {
            model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationId));
        }
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("rooms", roomPage.getDtoList());
        model.addAttribute("pageResponse", roomPage);
    }

    @GetMapping("/room/create")
    public String createRoom(@RequestParam(required = false) Long accommodationId, Model model) {
        // 사이드바 "객실 등록"은 accommodationId 없이 들어온다
        model.addAttribute("accommodationId", accommodationId);
        if (accommodationId != null && accommodationId != 0) {
            Integer companyId = companyService.getCompanyIdByAccommodationId(accommodationId);
            model.addAttribute("accommodations", accommodationService.getAccommodationsByCompanyId(companyId));
        } else {
            model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        }
        return "owner/room/create";
    }

    @PostMapping("/room/create")
    public String createRoom(@ModelAttribute RoomDTO roomDTO, RedirectAttributes ra) throws Exception {
        Long roomId = roomService.registerRoom(roomDTO);

        // 대표 이미지는 선택 사항 (빈 파일은 올리지 않는다)
        MultipartFile mainFile = roomDTO.getFile();
        if (mainFile != null && !mainFile.isEmpty()) {
            String mainImage = fileStorage.upload(mainFile, "rooms/" + roomId);
            roomService.updateRoomImage(RoomImageDTO.builder()
                    .roomId(roomId)
                    .imageUrl(mainImage)
                    .sortOrder(0)
                    .build());
        }
        roomService.modifyRoomImage(roomId, roomDTO.getImages());

        ra.addFlashAttribute("success", "객실을 등록했습니다.");
        return "redirect:/owner/accommodation/" + roomDTO.getAccommodationId() + "/rooms";
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
    public String modifyRoom(@ModelAttribute RoomDTO roomDTO, RedirectAttributes ra) throws Exception {
        roomService.modifyRoom(roomDTO);
        MultipartFile mainFile = roomDTO.getFile();
        if (mainFile != null && !mainFile.isEmpty()) {
            roomService.replaceMainImage(roomDTO.getRoomId(), fileStorage.upload(mainFile, "rooms/" + roomDTO.getRoomId()));
        }
        roomService.modifyRoomImage(roomDTO.getRoomId(), roomDTO.getImages());
        ra.addFlashAttribute("success", "객실 정보를 수정했습니다.");
        return "redirect:/owner/room/list";
    }

    /** 판매 중지(INACTIVE). 예약 FK 때문에 행은 지우지 않는다. */
    @PostMapping("/room/{roomId}/delete")
    public String deleteRoom(@PathVariable Long roomId, RedirectAttributes ra) {
        roomService.removeRoom(roomId);
        ra.addFlashAttribute("success", "객실을 판매 중지(비활성)했습니다.");
        return "redirect:/owner/room/list";
    }

    @PostMapping("/room/{roomId}/activate")
    public String activateRoom(@PathVariable Long roomId, RedirectAttributes ra) {
        roomService.activateRoom(roomId);
        ra.addFlashAttribute("success", "객실을 다시 판매 가능으로 바꿨습니다.");
        return "redirect:/owner/room/inActive/list";
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
    public String reviews(@PathVariable Long roomId) {
        return "redirect:/owner/review/room/" + roomId;
    }
}
