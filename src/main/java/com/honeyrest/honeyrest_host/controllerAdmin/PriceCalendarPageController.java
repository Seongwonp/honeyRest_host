package com.honeyrest.honeyrest_host.controllerAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dto.PriceCalendarDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;

import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.service.PriceCalendarService;
import com.honeyrest.honeyrest_host.service.accommodation.AccommodationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Log4j2
@Controller
@RequestMapping("/admin/price")
@RequiredArgsConstructor
public class PriceCalendarPageController {

    private final PriceCalendarService priceCalendarService;
    private final AccommodationRepository accommodationRepository;
    private final CompanyRepository companyRepository;
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

    /**
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

        Long resolvedRoomId = (roomId == null || roomId.isBlank() || "null".equalsIgnoreCase(roomId) ? null : Long.valueOf(roomId));

        YearMonth yearMonth = (ym == null || ym.isBlank() || "null".equalsIgnoreCase(ym)) ? YearMonth.now() : YearMonth.parse(ym);
        YearMonth preYm = yearMonth.minusMonths(1);   // 이전달
        YearMonth nextYm = yearMonth.plusMonths(1);   // 다음달은 plus

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
     * 단건 업서트 (페이지 내 인라인 폼용)
     */
    @PostMapping("/upsert")
    public String upsert(@RequestParam Long companyId,
                         @RequestParam(required = false) Long accommodationId,
                         @RequestParam Long roomId,
                         @RequestParam String ym,
                         @RequestParam LocalDate date,
                         @RequestParam Long roomIdParam, // 실제 저장 데이터
                         @RequestParam(required = false) BigDecimal price,
                         @RequestParam(required = false, defaultValue = "calendar") String mode,
                         @RequestParam(required = false) Integer available,
                         @RequestParam(required = false) Integer minAvailable,

                         RedirectAttributes ra) {

        boolean created = priceCalendarService.upsert(roomIdParam, date, price, available);
        ra.addFlashAttribute("toast", created ? "신규 생성 완료" : "수정 완료");

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
}