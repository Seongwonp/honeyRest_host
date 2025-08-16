package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationResponseDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationUpdateRequestDTO;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Super;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/accommodations")
public class AccommodationAPIController {

    private final AccommodationService accommodationService;

    @Operation(summary = "숙소 전체 조회")
    @GetMapping
    public List<AccommodationResponseDTO> list() {
        return accommodationService.getAll();
    }

    // 숙소 ID로 조회
    @Operation(summary = "숙소 Id 조회")
    @GetMapping("/{id}")
    public AccommodationResponseDTO getById(@PathVariable Long id) {
        return accommodationService.getById(id);
    }

    // 숙소 등록
    @Operation(summary = "숙소 등록")
    @PostMapping
    public ResponseEntity<AccommodationResponseDTO> create(@Valid @RequestBody AccommodationCreateRequestDTO req) {
        AccommodationResponseDTO saved = accommodationService.create(req);
        return ResponseEntity.created(URI.create("/api/admin/accommodations/" + saved.getAccommodationId()))
                .body(saved);
    }

    // 숙소 수정
    @Operation(summary = "숙소 수정")
    @PutMapping("/{id}")
    public AccommodationResponseDTO update(@PathVariable Long id, @Valid @RequestBody AccommodationUpdateRequestDTO req) {
        return accommodationService.update(id, req);
    }

    // 숙소 삭제
    @Operation(summary = "숙소 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accommodationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}