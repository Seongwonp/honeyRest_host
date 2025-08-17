package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationListDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationResponseDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationUpdateRequestDTO;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/accommodations")
@Tag(name = "Accommodations", description = "숙소 관리 API")
public class AccommodationAPIController {

    private final AccommodationService accommodationService;

    @Operation(summary = "숙소 목록 조회(페이징/검색)",
            description = "예) /api/admin/accommodations?page=0&size=10&q=강남&categoryId=1&mainRegionId=11")
    @GetMapping
    public Page<AccommodationListDTO> search(
            @Parameter(description = "검색어(숙소명/주소)") @RequestParam(required = false) String q,
            @Parameter(description = "카테고리 ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "지역(대) ID") @RequestParam(required = false) Long mainRegionId,
            @ParameterObject Pageable pageable
    ) {
        return accommodationService.search(q, categoryId, mainRegionId, pageable);
    }

    @Operation(summary = "숙소 단건 조회")
    @GetMapping("/{id}")
    public AccommodationResponseDTO getById(@PathVariable Long id) {
        return accommodationService.getById(id);
    }

    @Operation(summary = "숙소 등록")
    @PostMapping
    public org.springframework.http.ResponseEntity<AccommodationResponseDTO> create(
            @Valid @RequestBody AccommodationCreateRequestDTO req) {
        AccommodationResponseDTO saved = accommodationService.create(req);
        return org.springframework.http.ResponseEntity
                .created(URI.create("/api/admin/accommodations/" + saved.getAccommodationId()))
                .body(saved);
    }

    @Operation(summary = "숙소 수정")
    @PutMapping("/{id}")
    public AccommodationResponseDTO update(@PathVariable Long id,
                                           @Valid @RequestBody AccommodationUpdateRequestDTO req) {
        return accommodationService.update(id, req);
    }

    @Operation(summary = "숙소 삭제")
    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<Void> delete(@PathVariable Long id) {
        accommodationService.delete(id);
        return org.springframework.http.ResponseEntity.noContent().build();
    }
}