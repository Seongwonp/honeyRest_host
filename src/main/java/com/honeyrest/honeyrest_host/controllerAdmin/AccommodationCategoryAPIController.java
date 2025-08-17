package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCategoryDTO;
import com.honeyrest.honeyrest_host.service.AccommodationCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/accommodationCategories")
public class AccommodationCategoryAPIController {

    private final AccommodationCategoryService accommodationCategoryService;

    @Operation(summary = "카테고리 전체 조회(정렬 포함)")
    @GetMapping
    public List<AccommodationCategoryDTO> list() {
        return accommodationCategoryService.list();
    }

    @Operation(summary = "카테고리 단건 조회")
    @GetMapping("{id}")
    private AccommodationCategoryDTO get(@PathVariable Long id) {
        return accommodationCategoryService.get(id);
    }
}
