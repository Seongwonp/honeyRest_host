package com.honeyrest.honeyrest_host.controllerAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dtoAdmin.DailyOverviewDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.GridCellDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.PriceCalendarDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyResourceAccessService;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyService;
import com.honeyrest.honeyrest_host.serviceAdmin.PriceCalendarService;
import com.honeyrest.honeyrest_host.serviceAdmin.RoomService;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final CompanyResourceAccessService resourceAccessService;

    /** 클라이언트가 보낸 companyId 는 신뢰하지 않고 로그인 사용자 기준으로 다시 계산한다. */
    private Integer requireOwnCompanyId(Authentication authentication) {
        Integer companyId = resourceAccessService.currentCompanyId(authentication);
        if (companyId == null) {
            throw new AccessDeniedException("회사 정보를 확인할 수 없습니다.");
        }
        return companyId;
    }




    /**
     * companyId 없이 들어오면 로그인 사용자 기준으로 채워서 리다이렉트
     */
    @GetMapping("/page")
    public String pageWithoutCompanyId(@RequestParam(required = false) String ym) {
        // 쿼리스트링의 companyId 는 무시하고 로그인 사용자의 회사로 리다이렉트
        Integer companyId = companyService.getCompanyIdByOfCurrentUser();
        if (companyId == null) return "redirect:/admin/dashboard";
        // ym 없으면 현재 월로
        String resolvedYm = (ym != null && !ym.isBlank()) ? ym : YearMonth.now().toString();
        return "redirect:/admin/price/page?companyId=" + companyId + "&ym=" + resolvedYm;
    }

    /*
     * 월 캘린더 페이지 (companyId 필수 버전)
     */
    @GetMapping(value = "/page", params = "companyId")
    public String page(Authentication authentication,
                       @RequestParam Integer companyId,
                       @RequestParam(required = false) Long accommodationId,
                       @RequestParam(required = false) String roomId,
                       @RequestParam(required = false) String ym,
                       @RequestParam(required = false) Integer minAvailable,
                       @RequestParam(required = false, defaultValue = "calendar") String mode,
                       Model model) {

        // 쿼리스트링의 companyId 는 표시용. 조회는 로그인 사용자의 회사로만 한다
        companyId = requireOwnCompanyId(authentication);

        // 1) 파라미터 해석
        Long resolvedRoomId = (roomId == null || roomId.isBlank() || "null".equalsIgnoreCase(roomId) ? null : Long.valueOf(roomId));
        if (resolvedRoomId != null && !resourceAccessService.ownsRoom(companyId, resolvedRoomId)) {
            throw new AccessDeniedException("해당 객실에 접근할 권한이 없습니다.");
        }
        if (accommodationId != null && !resourceAccessService.ownsAccommodation(companyId, accommodationId)) {
            throw new AccessDeniedException("해당 숙소에 접근할 권한이 없습니다.");
        }


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
    // upsert 는 roomId 만으로 동작하므로 roomId 가 로그인 회사 소유인지 확인한다(가격 변조 방지)
    @PostMapping("/upsert")
    @PreAuthorize("@companyAccess.ownsRoom(#roomId, authentication)")
    public String upsert(Authentication authentication,
                         @RequestParam Integer companyId,
                         @RequestParam(required = false) Long accommodationId,
                         @RequestParam Long roomId,
                         @RequestParam String ym,
                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                         @RequestParam(required = false) BigDecimal price,
                         @RequestParam(required = false, defaultValue = "calendar") String mode,
                         @RequestParam(required = false) Integer minAvailable,

                         RedirectAttributes ra) {

        companyId = requireOwnCompanyId(authentication);

        // 재고는 받지 않는다. 사용자 API 는 "총 객실 − 겹치는 예약 수"로 재고를 계산하므로
        // 판매 중지는 객실 상태나 총 객실 수로 한다. null 은 기존 값 유지.
        boolean created = priceCalendarService.upsert(roomId, date, price, null);
        // 관리자 레이아웃은 success/msg/error 키만 토스트로 표시한다
        ra.addFlashAttribute("success", created ? "신규 생성 완료" : "수정 완료");

        // PRG 패턴: 기존 쿼리스트링(회사/숙소/월/필터) 유지 리다이렉트
        StringBuilder redirect = new StringBuilder("redirect:/admin/price/page")
                .append("?companyId=").append(companyId)
                .append("&ym=").append(ym)
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
    @PreAuthorize("@companyAccess.ownsRoom(#roomId, authentication)")
    public String bulkUpsert(Authentication authentication,
                             @RequestParam Integer companyId,
                             @RequestParam(required = false) Long accommodationId,
                             @RequestParam Long roomId,
                             @RequestParam String ym,
                             @RequestParam("json") String json,
                             @RequestParam(required = false, defaultValue = "calendar") String mode,
                             @RequestParam(required = false) Integer minAvailable,
                             RedirectAttributes ra) {
        companyId = requireOwnCompanyId(authentication);
        try {
            PriceCalendarDTO priceCalendarDTO =
                    objectMapper.readValue(json, PriceCalendarDTO.class);
            // payload 의 모든 room 이 로그인 회사 소유인지 확인(타사 roomId 섞기 방지)
            Integer verifiedCompanyId = companyId;
            boolean allOwned = priceCalendarDTO.getRooms() == null || priceCalendarDTO.getRooms().stream()
                    .allMatch(roomDTO -> roomDTO.getRoom() != null
                            && resourceAccessService.ownsRoom(verifiedCompanyId, roomDTO.getRoom().getRoomId()));
            if (!allOwned) {
                throw new AccessDeniedException("일부 객실에 대한 권한이 없습니다.");
            }
            priceCalendarService.bulkUpsert(priceCalendarDTO);
            ra.addFlashAttribute("success", "벌크 업서트 완료");
        } catch (AccessDeniedException e) {
            throw e;
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

    // 숙소가 로그인 회사 소유인지 먼저 확인한다
    @GetMapping("/calendar/{accommodationId}")
    @PreAuthorize("@companyAccess.ownsAccommodation(#accommodationId, authentication)")
    public String roomCalendar(Authentication authentication,
                               @PathVariable Long accommodationId,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                               Model model) {

        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        }
        Integer companyId = requireOwnCompanyId(authentication);

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
    @PreAuthorize("@companyAccess.ownsAccommodationIfPresent(#accommodationId, authentication)")
    @ResponseBody   // JSON 응답
    public List<DailyOverviewDTO> getDailyOverview(Authentication authentication,
                                                   @RequestParam Integer companyId,
                                                   @RequestParam(required = false) Long accommodationId,
                                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        companyId = requireOwnCompanyId(authentication);
        return priceCalendarService.getDailyOverview(companyId, accommodationId, start, end);
    }


    // 5) 그리드 셀 데이터 (Grid Cells)
    @GetMapping("/grid-cells")
    @PreAuthorize("@companyAccess.ownsAccommodationIfPresent(#accommodationId, authentication)")
    @ResponseBody   // JSON 응답
    public List<GridCellDTO> getGridCells(Authentication authentication,
                                          @RequestParam Integer companyId,
                                          @RequestParam(required = false) Long accommodationId,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        companyId = requireOwnCompanyId(authentication);
        return priceCalendarService.getGridCells(companyId, accommodationId, start, end);
    }

    @GetMapping("/daily-revenue")
    @PreAuthorize("@companyAccess.ownsAccommodationIfPresent(#accommodationId, authentication)")
    @ResponseBody
    public Map<LocalDate, BigDecimal> getDailyRevenue(Authentication authentication,
                                                      @RequestParam Integer companyId,
                                                      @RequestParam(required = false) Long accommodationId,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        companyId = requireOwnCompanyId(authentication);
        return priceCalendarService.getDailyRevenueByCheckin(companyId, accommodationId, start, end);
    }
    @GetMapping("/daily-revenue/checkin")
    @PreAuthorize("@companyAccess.ownsAccommodationIfPresent(#accommodationId, authentication)")
    @ResponseBody
    public Map<LocalDate, BigDecimal> getDailyRevenueByCheckin(
            Authentication authentication,
            @RequestParam Integer companyId,
            @RequestParam(required = false) Long accommodationId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        companyId = requireOwnCompanyId(authentication);
        return priceCalendarService.getDailyRevenueByCheckin(companyId, accommodationId, start, end);
    }


}