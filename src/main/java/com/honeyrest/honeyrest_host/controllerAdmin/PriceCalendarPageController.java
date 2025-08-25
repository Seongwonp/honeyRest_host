package com.honeyrest.honeyrest_host.controllerAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dto.PriceInventoryCalendarDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;

import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationCategoryRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.service.PriceCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.context.SecurityContextHolder;
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
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules(); // YearMonth/LocalDate 지원
    private final AccommodationCategoryRepository accommodationCategoryRepository;
    private final AccommodationRepository accommodationRepository;

    /** companyId 없이 들어오면 로그인 사용자 기준으로 채워서 리다이렉트 */
    @GetMapping("/page")
    public String pageWithoutCompanyId(@RequestParam(required = false) Long companyId,
                                       @RequestParam(required = false) String ym) {
        if (companyId == null) {
            companyId = resolveCompanyIdFromLogin();
        }
        // ym 없으면 현재 월로
        String redirectYm = (ym != null ? ym : YearMonth.now().toString());
        return "redirect:/admin/price/page?companyId=" + companyId + "&ym=" + redirectYm;
    }

    /** 월 캘린더 페이지 (companyId 필수 버전) */
    @GetMapping(value = "/page", params = "companyId")
    public String page(@RequestParam Long companyId,
                       @RequestParam(required = false) Long accommodationId,
                       @RequestParam(required = false) String ym,
                       @RequestParam(required = false) Integer minAvailable,
                       @RequestParam(required = false, defaultValue = "calendar") String mode,
                       Model model) {

        YearMonth yearMonth = normalizeYm(ym);
        YearMonth preYm = yearMonth.minusMonths(1);   // 이전달
        YearMonth nextYm = yearMonth.plusMonths(1);   // 다음달은 plus

        PriceInventoryCalendarDTO priceInventoryCalendarDTO =
                priceCalendarService.getMonth(companyId, accommodationId, yearMonth, minAvailable);

        String accommodationName = null;
        if (accommodationId != null) {
            accommodationName = accommodationRepository.findById(accommodationId)
                    .map(Accommodation::getName)
                    .orElse("알 수 없음");
        }

        model.addAttribute("priceInventoryCalendarDTO", priceInventoryCalendarDTO);
        model.addAttribute("companyId", companyId);
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("accommodationName", accommodationName);
        model.addAttribute("ym", yearMonth);
        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("preYm", preYm);
        model.addAttribute("nextYm", nextYm);
        model.addAttribute("minAvailable", minAvailable);
        model.addAttribute("mode", mode);

        return "admin/price/page";
    }
    private YearMonth normalizeYm(String ym) {
        if (ym == null || ym.isBlank() || "null".equalsIgnoreCase(ym)) {
            return YearMonth.now();
        }
        return YearMonth.parse(ym); // 기대 포맷: yyyy-MM
    }

    /** 단건 업서트 (페이지 내 인라인 폼용) */
    @PostMapping("/upsert")
    public String upsert(@RequestParam Long companyId,
                         @RequestParam(required = false) Long accommodationId,
                         @RequestParam String ym,
                         @RequestParam Long roomId,
                         @RequestParam LocalDate date,
                         @RequestParam(required = false) BigDecimal price,
                         @RequestParam(required = false) Integer available,
                         RedirectAttributes ra) {

        boolean created = priceCalendarService.upsert(roomId, date, price, available);
        ra.addFlashAttribute("toast", created ? "신규 생성 완료" : "수정 완료");

        // PRG 패턴: 기존 쿼리스트링(회사/숙소/월/필터) 유지 리다이렉트
        StringBuilder redirect = new StringBuilder("redirect:/admin/price/page")
                .append("?companyId=").append(companyId)
                .append("&ym=").append(ym);
        if (accommodationId != null) redirect.append("&accommodationId=").append(accommodationId);
        return redirect.toString();
    }

    /** 벌크 업서트 (textarea JSON 전송용) */
    @PostMapping("/bulk-upsert")
    public String bulkUpsert(@RequestParam Long companyId,
                             @RequestParam(required = false) Long accommodationId,
                             @RequestParam String ym,
                             @RequestParam("json") String json,
                             RedirectAttributes ra) {
        try {
            PriceInventoryCalendarDTO payload =
                    objectMapper.readValue(json, PriceInventoryCalendarDTO.class);
            priceCalendarService.bulkUpsert(payload);
            ra.addFlashAttribute("toast", "벌크 업서트 완료");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "벌크 업서트 실패: " + e.getMessage());
        }

        StringBuilder redirect = new StringBuilder("redirect:/admin/price/page")
                .append("?companyId=").append(companyId)
                .append("&ym=").append(ym);
        if (accommodationId != null) redirect.append("&accommodationId=").append(accommodationId);
        return redirect.toString();
    }

    /** 로그인 email → companyId 조회 (FK 없다: 이메일 매칭 조인 사용) */
    private Long resolveCompanyIdFromLogin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("Unauthenticated");
        String email = auth.getName();
        return companyRepository.findCompanyIdByUserEmail(email)
                .orElseThrow(() -> new IllegalStateException("Company not found for email: " + email));
    }
}