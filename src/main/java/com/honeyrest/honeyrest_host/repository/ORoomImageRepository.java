package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.dtoOwner.RoomImageDTO;
import com.honeyrest.honeyrest_host.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ORoomImageRepository extends JpaRepository<RoomImage, Long> {
    List<RoomImage> findByRoom_RoomId(Long roomId);
}
