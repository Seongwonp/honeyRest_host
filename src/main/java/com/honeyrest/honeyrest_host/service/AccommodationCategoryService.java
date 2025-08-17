package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCategoryDTO;

import java.util.List;

public interface AccommodationCategoryService {
    List<AccommodationCategoryDTO> list();
    AccommodationCategoryDTO get(Long id);
}
