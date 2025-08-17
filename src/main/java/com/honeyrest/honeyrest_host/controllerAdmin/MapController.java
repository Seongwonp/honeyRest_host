package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class MapController {
    private final MapService mapService;

    @Operation(summary = "주소 api")
    @GetMapping("/geocode")
    public ResponseEntity<AccommodationCreateRequestDTO> geocode(@RequestParam String address) {
        AccommodationCreateRequestDTO dto = mapService.getCoordinates(address);
        return ResponseEntity.ok(dto);
    }
}
