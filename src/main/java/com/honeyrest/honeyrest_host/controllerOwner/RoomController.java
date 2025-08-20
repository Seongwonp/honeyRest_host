package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PriceCalendarDTO;
import com.honeyrest.honeyrest_host.dtoOwner.RoomDTO;
import com.honeyrest.honeyrest_host.entity.PriceCalendar;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.service.PriceCalendarService;
import com.honeyrest.honeyrest_host.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final AccommodationService accommodationService;
    private final CompanyService companyService;
    private final PriceCalendarService priceCalendarService;

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

    @GetMapping("/room/calendar")
    public String roomCalendar(Model model) {
        return "owner/room/calendar";
    }

    @GetMapping("/room/{roomId}/calendar")
    public String getCalendar(
            @PathVariable Long roomId,
            @RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, Model model) {

        LocalDate now = LocalDate.now();
        if (startDate == null) {
            startDate = now.withDayOfMonth(1); // 이번 달 1일
            endDate = now.withDayOfMonth(now.lengthOfMonth());
        }

        List<PriceCalendarDTO> calendars = priceCalendarService.getPriceCalendars(roomId, startDate, endDate);

        Map<LocalDate, PriceCalendarDTO> calendarMap = calendars.stream()
                        .collect(Collectors.toMap(PriceCalendarDTO::getDate, pc -> pc));

        model.addAttribute("calendarMap", calendarMap);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("roomId", roomId);

        return "owner/room/calendar";
    }
}
