package com.honeyrest.honeyrest_host.repository.accommodation;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationImageDTO;
import com.honeyrest.honeyrest_host.entity.AccommodationImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationImageRepository extends JpaRepository<AccommodationImage, Long> {
    List<AccommodationImage> findByAccommodationAccommodationIdOrderBySortOrderAsc(Long accId);
    void deleteByAccommodationAccommodationId(Long accId);
}
