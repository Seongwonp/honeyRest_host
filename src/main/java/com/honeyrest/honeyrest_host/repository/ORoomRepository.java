package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ORoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByAccommodation_AccommodationId(Long accommodationId);

    Page<Room> findByAccommodation_AccommodationId(Long accommodationId, Pageable pageable);

    List<Room> findByAccommodation_AccommodationIdAndNameContainingIgnoreCase(Long accommodationId, String name);

    Room findByRoomId(Long roomId);

    Room findByName(String name);

    Room findByAccommodation_AccommodationIdAndRoomId(Long id, Long name);
}
