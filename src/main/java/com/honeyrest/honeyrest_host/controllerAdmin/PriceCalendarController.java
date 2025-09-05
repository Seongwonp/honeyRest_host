package com.honeyrest.honeyrest_host.controllerAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dtoAdmin.DailyOverviewDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.GridCellDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.PriceCalendarDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyService;
import com.honeyrest.honeyrest_host.serviceAdmin.PriceCalendarService;
import com.honeyrest.honeyrest_host.serviceAdmin.RoomService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller("adminPriceCalendarController")
@RequestMapping("/admin/price")
@RequiredArgsConstructor
public class PriceCalendarController {

    private final PriceCalendarService priceCalendarService;
    private final RoomService roomService;
    private final ObjectMapper objectMapper;
    private final CompanyService companyService;
    private final AccommodationService accommodationService;




    /**
     * companyId 없이 들어오면 로그인 사용자 기준으로 채워서 리다이렉트
     */
    @GetMapping("/page")
    public String pageWithoutCompanyId(@RequestParam(required = false) Long companyId,
                                       @RequestParam(required = false) String ym) {
        if (companyId == null) {
            companyId = companyService.getCompanyIdByOfCurrentUser();
        }
        // ym 없으면 현재 월로
        String resolvedYm = (ym != null && !ym.isBlank()) ? ym : YearMonth.now().toString();
        return "redirect:/admin/price/page?companyId=" + companyId + "&ym=" + resolvedYm;
    }

    /*
     * 월 캘린더 페이지 (companyId 필수 버전)
     */
    @GetMapping(value = "/page", params = "companyId")
    public String page(@RequestParam Long companyId,
                       @RequestParam(required = false) Long accommodationId,
                       @RequestParam(required = false) String roomId,
                       @RequestParam(required = false) String ym,
                       @RequestParam(required = false) Integer minAvailable,
                       @RequestParam(required = false, defaultValue = "calendar") String mode,
                       Model model) {

        // 1) 파라미터 해석
        Long resolvedRoomId = (roomId == null || roomId.isBlank() || "null".equalsIgnoreCase(roomId) ? null : Long.valueOf(roomId));


        YearMonth yearMonth;
        try {
            yearMonth = (ym == null || ym.isBlank()) ? YearMonth.now() : YearMonth.parse(ym);
        } catch (DateTimeParseException e) {
            yearMonth = YearMonth.now();
        }

        YearMonth preYm = yearMonth.minusMonths(1);
        YearMonth nextYm = yearMonth.plusMonths(1);

        // 2) 숙소 드롭다운(회사 소속 숙소들)
        model.addAttribute("accommodations", accommodationService.getAllById(companyId));

        PriceCalendarDTO priceCalendarDTO =
                priceCalendarService.getMonth(companyId, accommodationId, yearMonth, minAvailable);
        String accommodationName = (accommodationId != null) ? accommodationService.getNameById(accommodationId) : null;


        model.addAttribute("priceCalendarDTO", priceCalendarDTO);
        model.addAttribute("companyId", companyId);
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("accommodationName", accommodationName);
        model.addAttribute("roomId", resolvedRoomId);
        model.addAttribute("ym", yearMonth);
        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("preYm", preYm);
        model.addAttribute("nextYm", nextYm);
        model.addAttribute("minAvailable", minAvailable);
        model.addAttribute("mode", mode);

        return "admin/price/page";
    }

    /**
     * 단건 (페이지 내 인라인 폼용)
     */
    @PostMapping("/upsert")
    public String upsert(@RequestParam Long companyId,
                         @RequestParam(required = false) Long accommodationId,
                         @RequestParam Long roomId,
                         @RequestParam String yearMonth,
                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                         @RequestParam(required = false) BigDecimal price,
                         @RequestParam(required = false, defaultValue = "calendar") String mode,
                         @RequestParam(required = false) Integer available,
                         @RequestParam(required = false) Integer minAvailable,

                         RedirectAttributes ra) {

        boolean created = priceCalendarService.upsert(roomId, date, price, available);
        ra.addFlashAttribute("toast", created ? "신규 생성 완료" : "수정 완료");

        // PRG 패턴: 기존 쿼리스트링(회사/숙소/월/필터) 유지 리다이렉트
        StringBuilder redirect = new StringBuilder("redirect:/admin/price/page")
                .append("?companyId=").append(companyId)
                .append("&ym=").append(yearMonth.toString())
                .append("&mode=").append(mode);
        if (accommodationId != null) redirect.append("&accommodationId=").append(accommodationId);
        if (roomId != null) redirect.append("&roomId=").append(roomId);
        if (minAvailable != null) redirect.append("&minAvailable=").append(minAvailable);
        return redirect.toString();
    }

    /**
     * 벌크 업서트 (textarea JSON 전송용)
     */
    @PostMapping("/bulk-upsert")
    public String bulkUpsert(@RequestParam Long companyId,
                             @RequestParam(required = false) Long accommodationId,
                             @RequestParam Long roomId,
                             @RequestParam String ym,
                             @RequestParam("json") String json,
                             @RequestParam(required = false, defaultValue = "calendar") String mode,
                             @RequestParam(required = false) Integer minAvailable,
                             RedirectAttributes ra) {
        try {
            PriceCalendarDTO priceCalendarDTO =
                    objectMapper.readValue(json, PriceCalendarDTO.class);
            priceCalendarService.bulkUpsert(priceCalendarDTO);
            ra.addFlashAttribute("toast", "벌크 업서트 완료");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "벌크 업서트 실패: " + e.getMessage());
        }

        // 기존 선택/ 필터 유지
        StringBuilder redirect = new StringBuilder("redirect:/admin/price/page")
                .append("?companyId=").append(companyId)
                .append("&ym=").append(ym)
                .append("&mode=").append(mode);
        if (accommodationId != null) redirect.append("&accommodationId=").append(accommodationId);
        if (roomId != null) redirect.append("&roomId=").append(roomId);
        if (minAvailable != null) redirect.append("&minAvailable=").append(minAvailable);
        return redirect.toString();
    }

    @GetMapping("/calendar/{accommodationId}")
    public String roomCalendar(@PathVariable Long accommodationId,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                               Model model) {

        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        }
        Long companyId = companyService.getCompanyIdByAccommodationId(accommodationId);

        // 해당 숙소의 방 리스트
        List<RoomDTO> roomList = roomService.getRoomsByAccommodationId(accommodationId);

        // 방별 캘린더 데이터 (날짜별 가격/재고)
        Map<Long, Map<LocalDate, PriceCalendarDTO>> calendarDataMap = new HashMap<>();
        for (RoomDTO room : roomList) {
            Map<LocalDate, PriceCalendarDTO> roomCalendar =
                    priceCalendarService.getCalendarData(room.getRoomId(), startDate, endDate);
            calendarDataMap.put(room.getRoomId(), roomCalendar);
        }

        model.addAttribute("companyId", companyId);

        // 드롭다운용 전체 회사/숙소 목록
        model.addAttribute("accommodations", accommodationService.getAllById(companyId));

        // 캘린더 데이터
        model.addAttribute("roomList", roomList);
        model.addAttribute("calendarDataMap", calendarDataMap);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        LocalDate firstDayOfMonth = startDate.withDayOfMonth(1);
        DayOfWeek firstWeekday = firstDayOfMonth.getDayOfWeek();
        int startOffset = firstWeekday.getValue() % 7; // 일요일=0

        int daysInMonth = startDate.lengthOfMonth();
        int totalCells = daysInMonth + startOffset;

        model.addAttribute("startOffset", startOffset);
        model.addAttribute("daysInMonth", daysInMonth);
        model.addAttribute("totalCells", totalCells);


        return "admin/price/page";
    }

    // 4) 일자별 요약 (Daily Overview)

    @GetMapping("/daily-overview")
    @ResponseBody   // JSON 응답
    public List<DailyOverviewDTO> getDailyOverview(@RequestParam Long companyId,
                                                   @RequestParam(required = false) Long accommodationId,
                                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return priceCalendarService.getDailyOverview(companyId, accommodationId, start, end);
    }


    // 5) 그리드 셀 데이터 (Grid Cells)
    @GetMapping("/grid-cells")
    @ResponseBody   // JSON 응답
    public List<GridCellDTO> getGridCells(@RequestParam Long companyId,
                                          @RequestParam(required = false) Long accommodationId,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return priceCalendarService.getGridCells(companyId, accommodationId, start, end);
    }

    @GetMapping("/daily-revenue")
    @ResponseBody
    public Map<LocalDate, BigDecimal> getDailyRevenue(@RequestParam Long companyId,
                                                      @RequestParam(required = false) Long accommodationId,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return priceCalendarService.getDailyRevenueByCheckin(companyId, accommodationId, start, end);
    }
    @GetMapping("/daily-revenue/checkin")
    @ResponseBody
    public Map<LocalDate, BigDecimal> getDailyRevenueByCheckin(
            @RequestParam Long companyId,
            @RequestParam(required = false) Long accommodationId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        return priceCalendarService.getDailyRevenueByCheckin(companyId, accommodationId, start, end);
    }


}