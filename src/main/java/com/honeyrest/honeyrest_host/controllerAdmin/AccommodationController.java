package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dto.AccommodationDTO;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;

    // 전체 숙소 조회
    @GetMapping("/list")
    public ResponseEntity<List<AccommodationDTO>> list() {
        return ResponseEntity.ok(accommodationService.getAllAccommodations());
    }

    // 숙소 ID로 조회
    @GetMapping("/{id}")
        public ResponseEntity<AccommodationDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(accommodationService.getByAccommodationId(id));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // 새 숙소 등록
    @PostMapping
    public AccommodationDTO save(@RequestBody AccommodationDTO dto) {
        return accommodationService.registerAccommodation(dto);
    }

    // 숙소 수정
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody AccommodationDTO dto) {
        if (dto.getAccommodationId() == null) {
            dto.setAccommodationId(id);
        } else if (!dto.getAccommodationId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path id와 Body id가 일치하지 않습니다.");
        }
        accommodationService.modifyAccommodation(dto);
        return ResponseEntity.noContent().build();
    }

    // 숙소 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            accommodationService.removeAccommodation(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}