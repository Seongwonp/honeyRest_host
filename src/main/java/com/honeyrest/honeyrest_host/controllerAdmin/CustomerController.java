package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.UserDetailDTO;
import com.honeyrest.honeyrest_host.dto.UserListDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
@RequestMapping("admin/customers")
public class CustomerController {
    private final CustomerService customerService;

    // 고객 목록(요약: 포인트/예약수/리뷰수/평점)
    @GetMapping("/list")
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Integer page,
                       @RequestParam(required = false) Integer size,
                       @RequestParam(required = false) String sort, Model model) {

        Page<UserListDTO> result = customerService.list(q, page, size, sort);

        model.addAttribute("page", result);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("size", result.getSize());
        return "admin/customers/list";

    }

    // 고객 상세(예약 히스토리/리뷰 모아보기)
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        UserDetailDTO dto = customerService.getDetail(id);
        model.addAttribute("user", dto);
        return "admin/customers/detail";
    }
}
