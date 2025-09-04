package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationTagDTO;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;

public interface AccommodationTagService {
    // 태그 전체
    List<AccommodationTagDTO> findAll();

    // 카테고리별 그룹핑
    Map<String, List<AccommodationTagDTO>> findAllGroupedByCategory();

    @Transactional(readOnly = true)
    List<AccommodationTagDTO> findByAccommodationId(Long accommodationId);

    @Transactional(readOnly = true)
    List<AccommodationTagDTO> findByIds(List<Long> tagIds);

    void replaceMapping(Long accommodationId, List<Long> tagIds);
}

