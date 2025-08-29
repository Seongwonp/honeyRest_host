package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.RoomImageDTO;
import com.honeyrest.honeyrest_host.entity.RoomImage;

import java.util.List;

public interface RoomImageService {
    void registerRoomImage(RoomImageDTO roomImageDTO);

    // 이미지 교체하기
    void replaceAll(Long roomId, List<RoomImageDTO> images);

    void addImages(Long roomId, List<RoomImageDTO> images);

    void deleteImage(Long imageId);

    void deleteAllOfRoom(Long roomId);
}
