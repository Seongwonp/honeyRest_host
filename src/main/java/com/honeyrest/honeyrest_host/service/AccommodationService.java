package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationResponseDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationUpdateRequestDTO;

import java.util.List;

public interface AccommodationService {
    // 목록
    List<AccommodationResponseDTO> getAll();

    // id 조회
    AccommodationResponseDTO getById(Long id);

    // 등록
    AccommodationResponseDTO create(AccommodationCreateRequestDTO req);

    // 수정
    AccommodationResponseDTO update(Long id, AccommodationUpdateRequestDTO req);

    // 삭제
    void delete(Long id);
}