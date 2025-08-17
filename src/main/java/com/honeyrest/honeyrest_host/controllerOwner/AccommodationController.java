package com.honeyrest.honeyrest_host.controllerOwner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;

    // 전체 숙소 조회
    @GetMapping
    public List<AccommodationDTO> findAll() {
        return accommodationService.getAllAccommodations();
    }

    // 숙소 ID로 조회
    @GetMapping("/{id}")
    public AccommodationDTO findById(@PathVariable Long id) {
        return accommodationService.getByAccommodationId(id);
    }

    // 새 숙소 등록
    @PostMapping
    public Long save(@RequestBody AccommodationDTO dto) throws JsonProcessingException {
        return accommodationService.registerAccommodation(dto);
    }

    // 숙소 수정
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody AccommodationDTO dto) throws JsonProcessingException {
        accommodationService.modifyAccommodation(dto);
    }

    // 숙소 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        accommodationService.removeAccommodation(id);
    }
}
