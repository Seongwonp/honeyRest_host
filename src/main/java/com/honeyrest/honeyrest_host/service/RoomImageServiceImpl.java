package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.RoomDTO;
import com.honeyrest.honeyrest_host.dto.RoomImageDTO;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.entity.RoomImage;
import com.honeyrest.honeyrest_host.repository.RoomImageRepository;
import com.honeyrest.honeyrest_host.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Transactional
@Service
@RequiredArgsConstructor
public class RoomImageServiceImpl implements RoomImageService {

    private final RoomImageRepository roomImageRepository;
    private final RoomRepository roomRepository;

    // dto -> 엔티티
    private RoomImage toEntity(Long roomId, RoomImageDTO ri) {
        Room room = roomRepository.getReferenceById(roomId);
        return RoomImage.builder()
                .room(room)
                .imageUrl(ri.getImageUrl())
                .sortOrder(ri.getSortOrder())
                .build();
    }
    private RoomImageDTO toDTO(RoomImage e) {
        return RoomImageDTO.builder()
                .roomId(e.getRoom().getRoomId())
                .imageUrl(e.getImageUrl())
                .sortOrder(e.getSortOrder())
                // 엔티티에 type 이 없으니 정렬을 가져와서 0이면 메인, 아니면 서브로 나누기.
                .imageType(e.getSortOrder() != null && e.getSortOrder() == 0 ? "MAIN" : "SUB")
                .build();

    }

    private RoomImage roomImageEntity(RoomImageDTO roomImageDTO) {
        return RoomImage.builder()
                .imageUrl(roomImageDTO.getImageUrl())
                .build();
    }

    @Override
    public void registerRoomImage(RoomImageDTO roomImageDTO) {
        roomImageRepository.save(roomImageEntity(roomImageDTO));


    }
    @Override
    public void replaceAll(Long roomId, List<RoomImageDTO> images) {
        // 정렬/인덱스 보정: 가장 앞을 메인(0), 이후 1..N
        List<RoomImageDTO> normalized = normalize(images);
        roomImageRepository.deleteByRoomRoomId(roomId);
        List<RoomImage> entities = normalized.stream()
                .map(dto -> toEntity(roomId, dto))
                .toList();
        roomImageRepository.saveAll(entities);
    }

    @Override
    public void addImages(Long roomId, List<RoomImageDTO> images) {
        if (images == null || images.isEmpty()) return;
        // 현재 끝 sortOrder 파악
        List<RoomImage> current = roomImageRepository.findByRoomRoomIdOrderBySortOrderAsc(roomId);
        int next = current.isEmpty() ? 0 : (current.get(current.size() - 1).getSortOrder() == null ? current.size() : current.get(current.size() - 1).getSortOrder() + 1);

        List<RoomImage> added = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            RoomImageDTO dto = images.get(i);
            int order = next + i;
            added.add(toEntity(roomId, RoomImageDTO.builder()
                    .imageUrl(dto.getImageUrl())
                    .sortOrder(order)
                    .build()));
        }
        roomImageRepository.saveAll(added);
    }

    @Override
    public void deleteImage(Long imageId) {
        roomImageRepository.deleteById(imageId);
    }

    @Override
    public void deleteAllOfRoom(Long roomId) {
        roomImageRepository.deleteByRoomRoomId(roomId);
    }

    // 첫 번째 이미지를 MAIN(=0)으로 강제, 나머지는 1..N
    private List<RoomImageDTO> normalize(List<RoomImageDTO> images) {
        if (images == null || images.isEmpty()) return List.of();
        // 만약 sortOrder가 섞여 왔다면 첫 번째 요소 기준으로 재할당
        List<RoomImageDTO> copy = new ArrayList<>(images);
        // 혹시 정렬되어 있다면 유지, 아니면 지금 순서대로 사용
        copy.sort(Comparator.comparing(i -> i.getSortOrder() == null ? Integer.MAX_VALUE : i.getSortOrder()));
        for (int i = 0; i < copy.size(); i++) {
            copy.get(i).setSortOrder(i); // 0,1,2...
        }
        return copy;
    }

}
