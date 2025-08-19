package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.RoomDTO;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final AccommodationService accommodationService;
    private final CompanyService companyService;
    @GetMapping("/accommodation/{accommodationId}/rooms")
    public String roomsByAccommodation(@PathVariable Long accommodationId, Model model) {
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();
        List<CompanyDTO> companies = companyService.getAllCompanies();
        List<RoomDTO> roomDTOS;

        if (accommodationId != null) {
            roomDTOS = roomService.getRoomsByAccommodationId(accommodationId);
            model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationId));
        } else {
            roomDTOS = roomService.getAllRooms();
        }
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("rooms", roomDTOS);

        return "owner/room/list";
    }

    @GetMapping("/room/list")
    public String rooms(Model model) {
        model.addAttribute("accommodationId", 0);
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "owner/room/list";
    }

    @GetMapping("/room/create")
    public String createRoom(@RequestParam Long accommodationId, Model model) {
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        return "owner/room/create";
    }
    @PostMapping("/room/create")
    public String createRoom(@ModelAttribute RoomDTO roomDTO) {
        roomService.registerRoom(roomDTO);
        return "redirect:/owner/room/list";
    }
}
