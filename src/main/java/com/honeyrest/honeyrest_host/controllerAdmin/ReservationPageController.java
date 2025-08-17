package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ReservationPageController {

    private final ReservationService reservationService;

    // 예약 목록 페이지
    @GetMapping("/admin/reservations/list")
    public String reservationsListPage(Model model) {
        // 초기 필터/상태값 등 필요시 model.addAttribute(...)
        return "admin/reservations/list"; // templates/admin/reservations/list.html
    }

    // 예약 상세 페이지
    @GetMapping("/admin/reservations/detail")
    public String reservationDetailPage() {
        return "admin/reservations/detail"; // 예: ?number=... 로 프론트에서 API 호출
    }
}
