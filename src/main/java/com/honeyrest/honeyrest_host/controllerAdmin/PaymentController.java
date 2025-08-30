package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.PaymentDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.service.PaymentService;
import com.honeyrest.honeyrest_host.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Log4j2
@Controller
@RequestMapping("/admin/point")
@RequiredArgsConstructor
public class PaymentController {

    public final PaymentService paymentService;
    private final CompanyService companyService;
    private final ReservationService reservationService;

    /* 결제 내역 목록 */
    @GetMapping("/list")
    public String list(Authentication auth, @RequestParam(required = false) Long accommodationId, // 숙소 고유번호
                       @RequestParam(required = false) List<String> status, // 결제 상태
                       @RequestParam(required = false) List<String> method, // 결제수단
                       @RequestParam(required = false) String q, // 검색어
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from, // 시작일
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,   // 종료일
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        // 로그인 이메일 가져오기
        String email = (auth != null) ? auth.getName() : null;

        // 페이징
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));

        // 서비스 호출
        Page<PaymentDTO> result = paymentService.listForCompanyUser(email, accommodationId, status, method, q, from, to, pageable);

        // 모델 -> 뷰 전달
        model.addAttribute("page", result); // page 객체
        model.addAttribute("item", result.getContent()); // dto

        // 검색 조건 유지하기 위함
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("status", status);
        model.addAttribute("method", method);
        model.addAttribute("q", q);
        model.addAttribute("from", from);
        model.addAttribute("to", to);


        return "admin/point/list";

    }

    @GetMapping("/refunds")
    public String refunds(@AuthenticationPrincipal User user,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(defaultValue = "false") boolean includeUnpaidCancels,
                          @RequestParam(required = false) Long accommodationId,
                          @RequestParam(required = false) String q,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
                          Model model) {

        // 회사/사용자 맥락이 필요하면 로그인 이메일로 조회
        String loginEmail = (user != null) ? user.getEmail() : null;

        // 2) 회샤 id
        Long companyId = companyService.getCompanyIdByOfCurrentUser();

        // 결제 기준으로 '취소/환불'만 가져오기
        List<String> statuses = List.of("CANCELED", "REFUNDED","NO_SHOW");

        Page<PaymentDTO> result = paymentService.listForCompanyUser(
                loginEmail,       // 회사 구분이 필요하면 로그인 이메일 전달
                accommodationId,      // 특정 숙소만 필터링하면 값 전달
                statuses,
                Collections.emptyList(),       // methods(필요시 CARD/KAKAOPAY 등)
                q,from,to,
                PageRequest.of(Math.max(0, page - 1), size)
        );

        model.addAttribute("list", result.getContent());
        model.addAttribute("total", result.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", result.getTotalPages());
        // 미결제 취소 포함: 예약 기준
        if (includeUnpaidCancels) {
            PageRequestDTO pr = PageRequestDTO.builder().page(page).size(size).build();
            PageResponseDTO<ReservationDTO> unpaid = reservationService.getCompanyReservations(companyId, "CANCELLED", null, pr);
            model.addAttribute("unpaidList", unpaid.getDtoList());
            model.addAttribute("unpaidTotal",  unpaid.getTotal());
        }

        return "admin/point/refunds"; // (PaymentDTO 기준으로 필드 바꿔 출력)
    }
}