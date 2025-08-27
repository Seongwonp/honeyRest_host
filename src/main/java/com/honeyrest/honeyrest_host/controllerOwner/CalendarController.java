package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PriceCalendarDTO;
import com.honeyrest.honeyrest_host.dtoOwner.RoomDTO;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.service.ReservationService;
import com.honeyrest.honeyrest_host.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
public class CalendarController {
    private final RoomService roomService;
    private final CompanyService companyService;
    private final AccommodationService accommodationService;
    private final ReservationService reservationService;

    @GetMapping("/calendar/list")
    public String roomCalendar(Model model) {
        model.addAttribute("companies", companyService.getAllCompanies());

        return "owner/company/list";
    }

    @GetMapping("/calendar/accommodation")
    public String selectAccommodation(Model model) {
        model.addAttribute("companyId", 0);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        return "owner/calendar/accommodation";
    }

    @GetMapping("/calendar/{companyId}/{accommodationId}/calendar")
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


        return "owner/calendar/calendar";
    }
}
