package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.honeyrest_host.entity.AccommodationImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OAccommodationImageRepository extends JpaRepository<AccommodationImage, Long> {
    List<AccommodationImage> findByAccommodation_AccommodationId(Long id);
}
