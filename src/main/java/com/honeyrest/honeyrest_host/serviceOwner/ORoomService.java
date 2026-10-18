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

    /** 비활성 객실을 다시 판매 가능(ACTIVE)으로 */
    void activateRoom(Long id);

    /** 활성(active=true) 또는 비활성 객실 페이지. accommodationId 가 없으면 전체 */
    PageResponseDTO<RoomDTO> getRoomsByStatusWithPageable(Long accommodationId, boolean active, PageRequestDTO pageRequestDTO);

    PageResponseDTO<RoomDTO> getRoomsByAccommodationIdWithPageable(Long accommodationId, PageRequestDTO pageRequestDTO);

    void updateRoomImage(RoomImageDTO dto);

    List<RoomDTO> searchByNameContaining(Long accommodationId, String keyword);

    Long getRoomIdByReviewId(Long reviewId);

    RoomDTO getByAccommodationIdAndId(Long id, Long name);

    List<RoomImageDTO> getImagesByRoomId(Long roomId);

    void modifyRoomImage(Long roomId, List<MultipartFile> images) throws Exception;

    List<RoomImageDTO> getAllImages();

    /** 객실 대표 이미지 교체 (수정 화면의 대표 이미지 입력 — 과거에는 업로드해도 무시됐다) */
    void replaceMainImage(Long roomId, String imageUrl);
}
