package com.honeyrest.honeyrest_host.controllerAdmin;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ReviewPageController {

    @GetMapping("/admin/reviews")
    public String listPage() {
        return "admin/reviews/list";
    }

    @GetMapping("/admin/reviews/detail")
    public String detailPage() {
        return "admin/reviews/detail";
    }
}
