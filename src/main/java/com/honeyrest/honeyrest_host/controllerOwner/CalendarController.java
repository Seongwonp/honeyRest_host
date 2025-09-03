package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.serviceOwner.OAccommodationService;
import com.honeyrest.honeyrest_host.serviceOwner.OCompanyService;
import com.honeyrest.honeyrest_host.serviceOwner.OReservationService;
import com.honeyrest.honeyrest_host.serviceOwner.ORoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
@Log4j2
public class CalendarController {
    private final ORoomService roomService;
    private final OCompanyService companyService;
    private final OAccommodationService accommodationService;
    private final OReservationService reservationService;

    @GetMapping("/calendar/list")
    public String roomCalendar(@ModelAttribute PageRequestDTO pageRequestDTO, Model model) {
        if (pageRequestDTO.getPage() <= 0) pageRequestDTO.setPage(1);
        if (pageRequestDTO.getSize() <= 0) pageRequestDTO.setSize(10);

        PageResponseDTO<CompanyDTO> responseDTO = companyService.getCompaniesWithPage(pageRequestDTO);

        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("companies", responseDTO.getDtoList());
        log.info("responseDTO.getDtoList() = " + responseDTO.getDtoList());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        return "owner/calendar/list";
    }

    @GetMapping("/calendar/accommodation")
    public String selectAccommodation(Model model) {
        model.addAttribute("companyId", 0);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        return "owner/calendar/accommodation";
    }

    @GetMapping("/calendar/accommodation/{accommodationId}/calendar")
    public String roomCalendar(@PathVariable Long accommodationId,
                               @RequestParam(required = false) Long roomId,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                               Model model) {

        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        }
        Long companyId = companyService.getCompanyIdByAccommodationId(accommodationId);

        List<RoomDTO> roomList;
        // 해당 숙소의 방 리스트
        if (roomId == null) {
            roomList = roomService.getRoomsByAccommodationId(accommodationId);
        } else {
            roomList = Collections.singletonList(roomService.getByRoomId(roomId));
        }

        // 방별 캘린더 데이터 (날짜별 가격/재고)
        Map<Long, Map<LocalDate, PriceCalendarDTO>> calendarDataMap = new HashMap<>();
        for (RoomDTO room : roomList) {
            Map<LocalDate, PriceCalendarDTO> roomCalendar =
                    reservationService.getCalendarData(room.getRoomId(), startDate, endDate);
            calendarDataMap.put(room.getRoomId(), roomCalendar);
        }

        model.addAttribute("companyId", companyId);

        // 드롭다운용 전체 회사/숙소 목록

        model.addAttribute("roomId", roomId);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("rooms", roomService.getAllRooms());
        // 캘린더 데이터
        model.addAttribute("roomList", roomList);
        model.addAttribute("calendarDataMap", calendarDataMap);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("currentYear", startDate.getYear());
        model.addAttribute("currentMonth", startDate.getMonthValue());


        LocalDate firstDayOfMonth = startDate.withDayOfMonth(1);
        DayOfWeek firstWeekday = firstDayOfMonth.getDayOfWeek();
        int startOffset = firstWeekday.getValue() % 7; // 일요일=0

        int daysInMonth = startDate.lengthOfMonth();
        int totalCells = daysInMonth + startOffset;

        model.addAttribute("startOffset", startOffset);
        model.addAttribute("daysInMonth", daysInMonth);
        model.addAttribute("totalCells", totalCells);


        return "owner/calendar/calendar";
    }

    @GetMapping("/calendar/company/{companyId}/calendar")
    public String roomCompanyCalendar(@PathVariable Long companyId,
                               @RequestParam(required = false) Long accommodationId,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                               Model model) {

        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        }
        // 해당 숙소의 방 리스트
        List<RoomDTO> roomList;
        if (accommodationId == null) {
            roomList = roomService.getRoomsByAccommodationId(accommodationId);
        } else {
            roomList = roomService.getAllRooms();
        }

        // 방별 캘린더 데이터 (날짜별 가격/재고)
        Map<Long, Map<LocalDate, PriceCalendarDTO>> calendarDataMap = new HashMap<>();
        for (RoomDTO room : roomList) {
            Map<LocalDate, PriceCalendarDTO> roomCalendar =
                    reservationService.getCalendarData(room.getRoomId(), startDate, endDate);
            calendarDataMap.put(room.getRoomId(), roomCalendar);
        }
        model.addAttribute("companyId", companyId);

        // 드롭다운용 전체 회사/숙소 목록
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());

        // 캘린더 데이터
        model.addAttribute("roomList", roomList);
        model.addAttribute("calendarDataMap", calendarDataMap);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentYear", startDate.getYear());
        model.addAttribute("currentMonth", startDate.getMonthValue());

        LocalDate firstDayOfMonth = startDate.withDayOfMonth(1);
        DayOfWeek firstWeekday = firstDayOfMonth.getDayOfWeek();
        int startOffset = firstWeekday.getValue() % 7; // 일요일=0

        int daysInMonth = startDate.lengthOfMonth();
        int totalCells = daysInMonth + startOffset;

        model.addAttribute("startOffset", startOffset);
        model.addAttribute("daysInMonth", daysInMonth);
        model.addAttribute("totalCells", totalCells);


        return "owner/calendar/calendar";
    }
}
