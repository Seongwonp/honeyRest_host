package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dtoOwner.RoomDTO;

import java.util.List;

public interface RoomService {
    List<RoomDTO> getAllRooms();

    List<RoomDTO> getRoomsByAccommodationId(Long accommodationId);

    RoomDTO getByRoomId(Long id);

    Long registerRoom(RoomDTO dto);

    void modifyRoom(RoomDTO dto);

    void removeRoom(Long id);
}
