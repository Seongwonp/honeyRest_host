package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.RoomImageDTO;
import com.honeyrest.honeyrest_host.entity.RoomImage;
import com.honeyrest.honeyrest_host.repository.RoomImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class RoomImageServiceImpl implements RoomImageService {

    private final RoomImageRepository roomImageRepository;

    private RoomImage roomImageEntity(RoomImageDTO roomImageDTO) {
        return RoomImage.builder()
                .imageUrl(roomImageDTO.getImage())
                .build();
    }

    @Override
    public void registerRoomImage(RoomImageDTO roomImageDTO) {
        roomImageRepository.save(roomImageEntity(roomImageDTO));


    }
}
