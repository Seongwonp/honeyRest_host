package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByAccommodation_AccommodationId(Long accommodationId);

    // 페이징
    Page<Room> findByAccommodation_AccommodationId(Long accommodationId, Pageable pageable);
}