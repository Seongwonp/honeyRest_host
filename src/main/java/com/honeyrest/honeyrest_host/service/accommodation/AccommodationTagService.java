package com.honeyrest.honeyrest_host.service.accommodation;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationTagDTO;


import java.util.List;
import java.util.Map;

public interface AccommodationTagService {
    // 태그 전체
    List<AccommodationTagDTO> findAll();

    // 카테고리별 그룹핑
    Map<String, List<AccommodationTagDTO>> findAllGroupedByCategory();
}

