package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.service.ReservationService;
import com.honeyrest.honeyrest_host.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String roomCalendar(@ModelAttribute PageRequestDTO pageRequestDTO, Model model) {
        if (pageRequestDTO.getPage() <= 0) pageRequestDTO.setPage(1);
        if (pageRequestDTO.getSize() <= 0) pageRequestDTO.setSize(10);

        PageResponseDTO<CompanyDTO> responseDTO = companyService.getCompaniesWithPage(pageRequestDTO);

        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("companies", responseDTO.getDtoList());
        return "owner/calendar/company";
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
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        }

        CompanyDTO company = companyService.getCompany(companyId);
        AccommodationDTO accommodation = accommodationService.getByAccommodationId(accommodationId);

        // 해당 숙소의 방 리스트
        List<RoomDTO> roomList = roomService.getRoomsByAccommodationId(accommodationId);

        // 방별 캘린더 데이터 (날짜별 가격/재고)
        Map<Long, Map<LocalDate, PriceCalendarDTO>> calendarDataMap = new HashMap<>();
        for (RoomDTO room : roomList) {
            Map<LocalDate, PriceCalendarDTO> roomCalendar =
                    reservationService.getCalendarData(room.getRoomId(), startDate, endDate);
            calendarDataMap.put(room.getRoomId(), roomCalendar);
        }

        // 드롭다운용 전체 회사/숙소 목록
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());

        // 선택된 값
        model.addAttribute("company", company);
        model.addAttribute("accommodation", accommodation);

        // 캘린더 데이터
        model.addAttribute("roomList", roomList);
        model.addAttribute("calendarDataMap", calendarDataMap);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "owner/calendar/calendar";
    }
}
