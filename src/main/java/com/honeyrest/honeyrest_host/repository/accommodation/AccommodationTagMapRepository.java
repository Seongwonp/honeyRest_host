package com.honeyrest.honeyrest_host.repository.accommodation;

import com.honeyrest.honeyrest_host.entity.AccommodationTagMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationTagMapRepository extends JpaRepository<AccommodationTagMap, Long> {
    List<AccommodationTagMap> findByAccommodationAccommodationId(Long accId);
    void deleteByAccommodationAccommodationId(Long accId);
}
