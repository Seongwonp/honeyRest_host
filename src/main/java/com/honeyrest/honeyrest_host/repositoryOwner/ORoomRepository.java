package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.domain.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ORoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByAccommodation_AccommodationId(Long accommodationId);

    Page<Room> findByAccommodation_AccommodationId(Long accommodationId, Pageable pageable);

    List<Room> findByAccommodation_AccommodationIdAndNameContainingIgnoreCase(Long accommodationId, String name);

    Room findByRoomId(Long roomId);

    // 활성/비활성 객실 목록: 상태 조건을 쿼리에 넣어 total 과 목록 건수를 맞춘다
    @EntityGraph(attributePaths = {"accommodation"})
    Page<Room> findByStatusIgnoreCase(String status, Pageable pageable);

    @EntityGraph(attributePaths = {"accommodation"})
    Page<Room> findByStatusNotIgnoreCase(String status, Pageable pageable);

    @EntityGraph(attributePaths = {"accommodation"})
    Page<Room> findByAccommodation_AccommodationIdAndStatusIgnoreCase(Long accommodationId, String status, Pageable pageable);

    @EntityGraph(attributePaths = {"accommodation"})
    Page<Room> findByAccommodation_AccommodationIdAndStatusNotIgnoreCase(Long accommodationId, String status, Pageable pageable);

    Room findByName(String name);

    Room findByAccommodation_AccommodationIdAndRoomId(Long id, Long name);
}
