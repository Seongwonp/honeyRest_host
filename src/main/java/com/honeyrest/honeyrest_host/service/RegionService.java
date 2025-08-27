package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;


import java.util.List;

public interface RegionService {
    // 대지역 전체
    List<AccommodationCreateRequestDTO> findMainRegion();

    // 부모지역의 하위(소지역) 전체
    List<AccommodationCreateRequestDTO> findSubRegion();

    // 뷰에서 바로쓰기 리스트

}
