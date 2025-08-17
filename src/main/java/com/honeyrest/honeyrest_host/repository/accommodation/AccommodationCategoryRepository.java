package com.honeyrest.honeyrest_host.repository.accommodation;

import com.honeyrest.honeyrest_host.entity.AccommodationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationCategoryRepository extends JpaRepository<AccommodationCategory, Long> {
    List<AccommodationCategory> findAllByOrderBySortOrderAscCategoryIdAsc();
}