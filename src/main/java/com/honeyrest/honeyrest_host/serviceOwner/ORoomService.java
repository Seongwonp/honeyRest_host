package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoOwner.RoomDTO;
import com.honeyrest.honeyrest_host.dtoOwner.RoomImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ORoomService {
    List<RoomDTO> getAllRooms();

    List<RoomDTO> getRoomsByAccommodationId(Long accommodationId);

    RoomDTO getByRoomId(Long id);

    Long registerRoom(RoomDTO dto);

    void modifyRoom(RoomDTO dto);

    void removeRoom(Long id);

    PageResponseDTO<RoomDTO> getRoomsByAccommodationIdWithPageable(Long accommodationId, PageRequestDTO pageRequestDTO);

    void updateRoomImage(RoomImageDTO dto);

    List<RoomDTO> searchByNameContaining(Long accommodationId, String keyword);

    Long getRoomIdByReviewId(Long reviewId);

    RoomDTO getByAccommodationIdAndId(Long id, Long name);

    List<RoomImageDTO> getImagesByRoomId(Long roomId);

    void modifyRoomImage(Long roomId, List<MultipartFile> images) throws Exception;

    List<RoomImageDTO> getAllImages();
}
