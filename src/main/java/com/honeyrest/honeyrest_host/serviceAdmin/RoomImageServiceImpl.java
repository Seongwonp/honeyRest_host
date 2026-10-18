package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomImageDTO;
import com.honeyrest.domain.entity.Room;
import com.honeyrest.domain.entity.RoomImage;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomImageRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
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
    // 객실 이미지가 바뀌면 사용자 API 숙소 상세 캐시를 커밋 후 지운다
    private final SearchCacheInvalidator searchCacheInvalidator;

    private void evictAccommodationOfRoom(Long roomId) {
        roomRepository.findById(roomId)
                .map(Room::getAccommodation)
                .ifPresent(acc -> searchCacheInvalidator.evictAccommodationAfterCommit(acc.getAccommodationId()));
    }

    /**
     * 이미지를 명시적으로 지우기 전에, 같은 영속성 컨텍스트에 로딩된 Room 의 images 컬렉션에서도 빼 둔다.
     * Room.images 가 cascade=ALL 이라, 컬렉션이 로딩된 채로(예: 같은 트랜잭션의 객실 수정 merge) 이미지만 지우면
     * flush 때 Room 쪽 PERSIST cascade 가 삭제 예정 이미지를 다시 살려 삭제가 무시된다.
     * 컬렉션이 로딩되지 않았으면 아무것도 하지 않는다(추가 조회 없음).
     */
    private static void detachFromLoadedRoom(RoomImage image) {
        Room room = image.getRoom();
        if (room != null && Hibernate.isInitialized(room) && Hibernate.isInitialized(room.getImages())) {
            room.getImages().remove(image);
        }
    }

    /** 객실의 이미지 행을 모두 지운다 (로딩된 Room 컬렉션에서도 제거) */
    private void deleteImagesOfRoom(Long roomId) {
        List<RoomImage> images = roomImageRepository.findByRoomRoomIdOrderBySortOrderAsc(roomId);
        images.forEach(RoomImageServiceImpl::detachFromLoadedRoom);
        roomImageRepository.deleteAll(images);
        roomImageRepository.flush(); // 이어지는 새 이미지 저장보다 삭제가 먼저 반영되게 한다 (기존 deleteBy... 의 flush 와 같은 순서)
    }

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
        deleteImagesOfRoom(roomId);
        List<RoomImage> entities = normalized.stream()
                .map(dto -> toEntity(roomId, dto))
                .toList();
        roomImageRepository.saveAll(entities);
        evictAccommodationOfRoom(roomId);
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
        evictAccommodationOfRoom(roomId);
    }

    @Override
    public void deleteImage(Long imageId) {
        roomImageRepository.findById(imageId).ifPresent(img -> {
            evictAccommodationOfRoom(img.getRoom().getRoomId());
            detachFromLoadedRoom(img);
            roomImageRepository.delete(img);
        });
    }

    @Override
    public void deleteAllOfRoom(Long roomId) {
        deleteImagesOfRoom(roomId);
        evictAccommodationOfRoom(roomId);
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
