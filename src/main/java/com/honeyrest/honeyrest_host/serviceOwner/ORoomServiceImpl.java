package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.type.RoomStatus;

import jakarta.persistence.EntityNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.cache.RoomSearchSnapshot;
import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.domain.entity.Accommodation;
import com.honeyrest.domain.entity.Room;
import com.honeyrest.domain.entity.RoomImage;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OReviewRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomImageRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomRepository;
import com.honeyrest.honeyrest_host.storage.FileStorage;
import com.honeyrest.honeyrest_host.utilAdmin.RoomEntityCopy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ORoomServiceImpl implements ORoomService {
    private final ORoomRepository roomRepository;
    private final OAccommodationRepository accommodationRepository;
    private final ObjectMapper objectMapper;
    private final ORoomImageRepository roomImageRepository;
    private final OReviewRepository reviewRepository;
    private final FileStorage fileStorage;
    // 객실 재고(totalRooms)·상태·가격이 바뀌면 사용자 API 검색 캐시 세대를 커밋 후 올린다
    private final SearchCacheInvalidator searchCacheInvalidator;

    // ===== Mapper =====
    private RoomDTO toDTO(Room e) {
        return RoomDTO.builder()
                .roomId(e.getRoomId())
                .accommodationId(e.getAccommodation().getAccommodationId())
                .name(e.getName())
                .type(e.getType())
                .price(e.getPrice())
                .maxOccupancy(e.getMaxOccupancy())
                .standardOccupancy(e.getStandardOccupancy())
                .extraPersonFee(e.getExtraPersonFee())
                .bedInfo(parseString(e.getBedInfo()))
                .amenities(parseString(e.getAmenities()))
                .description(e.getDescription())
                .totalRooms(e.getTotalRooms())
                .status(e.getStatus())
                .accommodationName(e.getAccommodation().getName())
                .build();
    }

    /** 목록 썸네일: 정렬 순서가 가장 앞선 객실 이미지 */
    private RoomDTO withThumbnail(RoomDTO dto) {
        roomImageRepository.findByRoom_RoomId(dto.getRoomId()).stream()
                .min(Comparator.comparing(i -> i.getSortOrder() == null ? Integer.MAX_VALUE : i.getSortOrder()))
                .ifPresent(i -> dto.setThumbnailUrl(i.getImageUrl()));
        return dto;
    }

    /**
     * "wifi:true, tv:true" -> Map<String,Object> 변환
     */
    private List<String> parseList(String jsonInput) {
        if (jsonInput == null || jsonInput.isBlank()) return Collections.emptyList();

        // JSON 배열/CSV/여러 줄 모두 허용. 과거에는 JSON 배열만 읽어 bed_info 처럼 형식이 다른 값마다
        // 스택트레이스 경고를 남기고 빈 값을 보여 줬다.
        return com.honeyrest.honeyrest_host.utilAdmin.AmenitiesParser.toList(jsonInput);
    }
    private String parseString(String jsonInput) {
        List<String> list = parseList(jsonInput);
        return String.join(", ", list);
    }
    /** 신규 객실 엔티티 (등록용) */
    private Room toEntity(RoomDTO d) {
        return applyForm(Room.builder(), d).build();
    }

    /**
     * 객실 폼 값을 빌더에 채운다. 등록은 빈 빌더에, 수정은 관리 중인 Room 에서 시작한 빌더(RoomEntityCopy.of)에 쓴다.
     * 수정 폼은 모든 항목을 보내므로 값은 폼 그대로 덮어쓰고(기존 동작), 이미지 컬렉션·roomId 는 빌더에 남은 값을 쓴다.
     */
    private Room.RoomBuilder applyForm(Room.RoomBuilder b, RoomDTO d) {
        // 편의시설과 침대 정보를 각각 변환한다. 과거에는 편의시설이 비어 있으면 침대 정보까지 "[]" 로 버려졌고,
        // 공백 기준으로도 쪼개 "더블 침대" 가 ["더블","침대"] 로 저장됐다.
        String amenitiesJson = com.honeyrest.honeyrest_host.utilAdmin.AmenitiesParser.normalizeToJson(d.getAmenities());
        String bedInfoJson = com.honeyrest.honeyrest_host.utilAdmin.AmenitiesParser.normalizeToJson(d.getBedInfo());
        return b
                .accommodation(accommodationRepository.getReferenceById(d.getAccommodationId()))
                .name(d.getName())
                .type(d.getType())
                .price(d.getPrice())
                .maxOccupancy(d.getMaxOccupancy())
                .standardOccupancy(d.getStandardOccupancy())
                .extraPersonFee(d.getExtraPersonFee())
                .bedInfo(bedInfoJson)
                .amenities(amenitiesJson)
                .description(d.getDescription())
                .totalRooms(d.getTotalRooms())
                .status(d.getStatus() == null ? RoomStatus.ACTIVE : d.getStatus());
    }

    private RoomImage toRoomImageEntity(RoomImageDTO d) {
        return RoomImage.builder()
                .imageId(d.getImageId())
                .imageUrl(d.getImageUrl())
                .sortOrder(d.getSortOrder())
                .room(roomRepository.getReferenceById(d.getRoomId()))
                .build();
    }
    private RoomImageDTO toRoomImageDTO(RoomImage e) {
        return RoomImageDTO.builder()
                .imageId(e.getImageId())
                .imageUrl(e.getImageUrl())
                .sortOrder(e.getSortOrder())
                .roomId(e.getRoom().getRoomId())
                .build();
    }

    @Override
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public List<RoomDTO> getRoomsByAccommodationId(Long accommodationId) {
        return roomRepository.findByAccommodation_AccommodationId(accommodationId)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public RoomDTO getByRoomId(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("객실이 존재하지 않습니다."));
        return toDTO(room);
    }

    @Override
    public Long registerRoom(RoomDTO dto) {
        Accommodation acc = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new EntityNotFoundException("숙소가 존재하지 않습니다."));
        dto.setAccommodationId(acc.getAccommodationId());
        Long roomId = roomRepository.save(toEntity(dto)).getRoomId();
        searchCacheInvalidator.bumpAfterCommit(); // 새 객실 = 새 재고
        searchCacheInvalidator.evictAccommodationAfterCommit(acc.getAccommodationId()); // 사용자 숙소 상세(객실 목록) 캐시
        return roomId;

    }

    @Override
    public void modifyRoom(RoomDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("객실이 존재하지 않습니다."));
        RoomSearchSnapshot before = RoomSearchSnapshot.of(room); // save(merge) 전에 떠 둔다
        // 관리 중인 Room(이미지 컬렉션 포함)에서 시작한다. 과거에는 toEntity(dto) 로 새 Room 을 만들어 merge 해
        // 영속 Room 의 이미지 컬렉션이 빈 리스트로 바뀌었다 (Room.images cascade=ALL, RoomEntityCopy 참고).
        Room updated = applyForm(RoomEntityCopy.of(room), dto).build();
        roomRepository.save(updated);
        if (RoomSearchSnapshot.of(updated).differsFrom(before)) {
            searchCacheInvalidator.bumpAfterCommit();
        }
        searchCacheInvalidator.evictAccommodationAfterCommit(before.accommodationId());
        searchCacheInvalidator.evictAccommodationAfterCommit(RoomSearchSnapshot.of(updated).accommodationId());
    }

    /**
     * 객실 판매 중지(INACTIVE). 예약·가격 캘린더가 객실을 참조하므로 행을 지우면 FK 오류가 난다.
     * 비활성 객실 목록에서 다시 활성화할 수 있다.
     */
    @Override
    public void removeRoom(Long id) {
        changeStatus(id, RoomStatus.INACTIVE);
    }

    @Override
    public void activateRoom(Long id) {
        changeStatus(id, RoomStatus.ACTIVE);
    }

    private void changeStatus(Long id, String status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("객실이 존재하지 않습니다."));
        // 상태만 바꾼다. 과거에는 DTO(침대·편의시설을 "a, b" 문자열로 바꾼 값)를 거쳐 새 Room 을 만들어 merge 했다.
        roomRepository.save(RoomEntityCopy.of(room).status(status).build());
        searchCacheInvalidator.bumpAfterCommit();
        if (room.getAccommodation() != null) {
            searchCacheInvalidator.evictAccommodationAfterCommit(room.getAccommodation().getAccommodationId());
        }
    }

    @Override
    public PageResponseDTO<RoomDTO> getRoomsByStatusWithPageable(Long accommodationId, boolean active, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("roomId").descending());
        Page<Room> page;
        if (accommodationId != null && accommodationId > 0) {
            page = active
                    ? roomRepository.findByAccommodation_AccommodationIdAndStatusIgnoreCase(accommodationId, RoomStatus.ACTIVE, pageable)
                    : roomRepository.findByAccommodation_AccommodationIdAndStatusNotIgnoreCase(accommodationId, RoomStatus.ACTIVE, pageable);
        } else {
            page = active
                    ? roomRepository.findByStatusIgnoreCase(RoomStatus.ACTIVE, pageable)
                    : roomRepository.findByStatusNotIgnoreCase(RoomStatus.ACTIVE, pageable);
        }
        List<RoomDTO> list = page.getContent().stream().map(this::toDTO).map(this::withThumbnail).toList();
        return PageResponseDTO.<RoomDTO>withAll()
                .dtoList(list)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(page.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<RoomDTO> getRoomsByAccommodationIdWithPageable(Long accommodationId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("roomId").descending());

        Page<Room> page;
        if (accommodationId != null && accommodationId > 0) {
            page = roomRepository.findByAccommodation_AccommodationId(accommodationId, pageable);
        } else {
            page = roomRepository.findAll(pageable);
        }

        List<RoomDTO> list = page.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        long total = page.getTotalElements();

        return PageResponseDTO.<RoomDTO>withAll()
                .dtoList(list)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(total)
                .build();
    }

    @Override
    public void updateRoomImage(RoomImageDTO dto){
        roomImageRepository.save(toRoomImageEntity(dto));
        if (dto.getRoomId() != null) {
            roomRepository.findById(dto.getRoomId())
                    .map(Room::getAccommodation)
                    .ifPresent(acc -> searchCacheInvalidator.evictAccommodationAfterCommit(acc.getAccommodationId()));
        }
    }

    @Override
    public List<RoomDTO> searchByNameContaining(Long accommodationId, String keyword) {
        return roomRepository.findByAccommodation_AccommodationIdAndNameContainingIgnoreCase(accommodationId, keyword)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public Long getRoomIdByReviewId(Long reviewId){
        var review = reviewRepository.findByReviewId(reviewId);
        if (review == null) throw new EntityNotFoundException("리뷰를 찾을 수 없습니다. reviewId=" + reviewId);
        return review.getRoomId();
    }

    @Override
    public RoomDTO getByAccommodationIdAndId(Long id, Long name) {
        Room room = roomRepository.findByAccommodation_AccommodationIdAndRoomId(id, name);
        if (room == null) throw new EntityNotFoundException("객실을 찾을 수 없습니다.");
        return toDTO(room);
    }

    @Override
    public List<RoomImageDTO> getImagesByRoomId(Long roomId){
        return roomImageRepository.findByRoom_RoomId(roomId).stream().map(this::toRoomImageDTO).toList();
    }

    @Override
    public void modifyRoomImage(Long roomId, List<MultipartFile> images) throws Exception {
        if (images == null || images.isEmpty()) return;
        // 기존 이미지 뒤에 붙인다 (매번 0부터 매겨 대표 이미지와 순서가 겹쳤다)
        int sortOrder = roomImageRepository.findByRoom_RoomId(roomId).stream()
                .map(RoomImage::getSortOrder).filter(Objects::nonNull).max(Integer::compare).orElse(-1) + 1;

        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                String subImageUrl = fileStorage.upload(image, "rooms/" + roomId + "/images");

                RoomImageDTO imageDTO = RoomImageDTO.builder()
                        .imageUrl(subImageUrl)
                        .roomId(roomId)
                        .sortOrder(sortOrder++)
                        .build();

                updateRoomImage(imageDTO);
            }
        }

    }

    @Override
    public void replaceMainImage(Long roomId, String imageUrl) {
        // 대표 이미지(정렬 순서가 가장 앞선 이미지)의 URL 을 바꾼다. 없으면 0번으로 추가한다.
        Optional<RoomImage> current = roomImageRepository.findByRoom_RoomId(roomId).stream()
                .min(Comparator.comparing(i -> i.getSortOrder() == null ? Integer.MAX_VALUE : i.getSortOrder()));
        // merge 뒤에는 영속 엔티티의 URL 도 새 값으로 바뀌므로 지울 옛 URL 은 먼저 떠 둔다
        // (과거 순서대로면 방금 올린 새 파일을 지워 대표 이미지가 404 였다)
        String oldUrl = current.map(RoomImage::getImageUrl).orElse(null);
        updateRoomImage(RoomImageDTO.builder()
                .imageId(current.map(RoomImage::getImageId).orElse(null))
                .roomId(roomId)
                .imageUrl(imageUrl)
                .sortOrder(current.map(RoomImage::getSortOrder).orElse(0))
                .build());
        if (oldUrl != null && !oldUrl.equals(imageUrl)) {
            fileStorage.delete(oldUrl);
        }
    }

    @Override
    public List<RoomImageDTO> getAllImages(){
        return roomImageRepository.findAll().stream().map(this::toRoomImageDTO).toList();
    }
}
