package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.AccommodationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAccommodationCategoryRepository extends JpaRepository<AccommodationCategory, Long> {
}
