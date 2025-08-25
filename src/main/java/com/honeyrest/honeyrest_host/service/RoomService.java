package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.RoomDTO;
import com.honeyrest.honeyrest_host.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomService {
    // 전체 객실
    Page<RoomDTO> findPageAll(Pageable pageable);

    // 숙소별 객실
    Page<RoomDTO> findPageByAccommodationId(Long accommodationId, Pageable pageable);

    // 단건/CRUD
    RoomDTO getByRoomId(Long roomId);
    RoomDTO registerRoom(RoomDTO dto);
    void modifyRoom(RoomDTO dto);
    void removeRoom(Long roomId);
}