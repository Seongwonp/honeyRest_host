package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.PaymentDTO;
import com.honeyrest.honeyrest_host.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@Controller
@RequestMapping("/admin/point")
@RequiredArgsConstructor
public class PaymentController {

    public final PaymentService paymentService;

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


}
