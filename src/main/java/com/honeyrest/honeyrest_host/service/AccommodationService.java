package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationListDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccommodationService {
    // 목록
    List<AccommodationCreateRequestDTO> getAll();

    // id 조회
    AccommodationCreateRequestDTO getById(Long id);

    // 등록
    AccommodationCreateRequestDTO create(AccommodationCreateRequestDTO req);

    // 수정
    AccommodationCreateRequestDTO update(Long id, AccommodationUpdateRequestDTO req);

    // 삭제
    void delete(Long id);

    Page<AccommodationListDTO> search(String q, Long categoryId, Long mainRegionId, Pageable pageable);

    // 승인
    void changeStatus(Long id, String status); // "APPROVED" | "REJECTED" | "ACTIVE" 등

    long count();
    // 회사별 객실 조회
    Page<AccommodationListDTO> findByCompanyId(Long companyId, Pageable pageable);

    // 회사 + 상태별 조회(승인 대기 목록)
    Page<AccommodationListDTO> findByCategoryIdAndStatus(Long companyId, String status, Pageable pageable);
}