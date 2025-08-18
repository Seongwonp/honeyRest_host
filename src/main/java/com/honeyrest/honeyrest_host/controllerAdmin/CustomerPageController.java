package com.honeyrest.honeyrest_host.controllerAdmin;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class CustomerPageController {
    // 고객 목록(요약: 포인트/예약수/리뷰수/평점)
    @GetMapping("/admin/customers")
    public String customersListPage() {
        return "admin/customers/list";
    }

    // 고객 상세(예약 히스토리/리뷰 모아보기)
    @GetMapping("/admin/customers/detail")
    public String customersDetailPage() {
        return "admin/customers/detail";
    }
}
