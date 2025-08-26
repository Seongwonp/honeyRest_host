package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PriceCalendarDTO;
import com.honeyrest.honeyrest_host.dtoOwner.RoomDTO;
import com.honeyrest.honeyrest_host.entity.PriceCalendar;
import com.honeyrest.honeyrest_host.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/room/calendar")
    public String roomCalendar(Model model) {
        return "owner/room/calendar";
    }

    @GetMapping("/room/{companyId}/{accommodationId}/calendar")
    public String roomCalendar(@PathVariable Long companyId,
                               @PathVariable Long accommodationId,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                               Model model) {

        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }
        CompanyDTO company = companyService.getCompany(companyId);
        AccommodationDTO accommodation = accommodationService.getByAccommodationId(accommodationId);

        List<RoomDTO> roomList = roomService.getRoomsByAccommodationId(accommodationId);

        Map<Long, Map<LocalDate, PriceCalendarDTO>> calendarDataMap = new HashMap<>();
        for (RoomDTO room : roomList) {
            Map<LocalDate, PriceCalendarDTO> roomCalendar = reservationService.getCalendarData(room.getRoomId(), startDate, endDate);
            calendarDataMap.put(room.getRoomId(), roomCalendar);
        }

        model.addAttribute("company", company);
        model.addAttribute("accommodation", accommodation);
        model.addAttribute("roomList", roomList);
        model.addAttribute("calendarDataMap", calendarDataMap);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        log.info(company);
        log.info(accommodation);
        log.info(roomList);
        log.info(calendarDataMap);
        log.info(startDate);
        log.info(endDate);

        return "owner/room/calendar";
    }
}
