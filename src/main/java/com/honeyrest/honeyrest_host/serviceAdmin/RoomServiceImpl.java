package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.type.RoomStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.cache.RoomSearchSnapshot;
import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomImageDTO;
import com.honeyrest.domain.entity.RoomImage;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomImageRepository;
import jakarta.persistence.EntityNotFoundException;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomDTO;
import com.honeyrest.domain.entity.Accommodation;
import com.honeyrest.domain.entity.Room;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.utilAdmin.RoomEntityCopy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final AccommodationRepository accommodationRepository;
    private final ObjectMapper objectMapper;
    private final RoomImageRepository roomImageRepository;
    private final com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository reservationRepository;
    private final com.honeyrest.honeyrest_host.repositoryAdmin.price.PriceCalendarRepository priceCalendarRepository;
    // 객실 재고·상태·가격이 바뀌면 사용자 API 검색 캐시를 커밋 후 무효화
    private final SearchCacheInvalidator searchCacheInvalidator;

    private JsonNode stringToJsonNode(String json) {
        try {
            if (json == null || json.isBlank()) {
                return objectMapper.readTree("[]");
            }
            String s = json.trim();
            // 이미 JSON 형태면 그대로 파싱
            if ((s.startsWith("[") && s.endsWith("]")) || (s.startsWith("{") && s.endsWith("}"))) {
                return objectMapper.readTree(s);
            }
            // CSV 텍스트를 JSON 배열로 변환
            var arr = objectMapper.createArrayNode();
            for (String part : s.split(",")) {
                String item = part.trim();
                if (!item.isEmpty()) {
                    arr.add(item);
                }
            }
            return arr;
        } catch (Exception e) {
            try {
                return objectMapper.readTree("[]");
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private String jsonNodeToString(JsonNode node) {
        try {
            if (node == null || node.isNull()) return "[]";
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            return "[]";
        }
    }

    // ===== Mapper =====
    private RoomDTO toDTO(Room e) {
        RoomDTO dto = RoomDTO.builder()
                .roomId(e.getRoomId())
                .accommodationId(e.getAccommodation().getAccommodationId())
                //전체 목록에서 보기 좋도록 숙소명도 같이 내려주자 (DTO에 필드 하나 추가)
                .accommodationName(e.getAccommodation().getName())
                .roomName(e.getName())
                .type(e.getType())
                .price(e.getPrice())
                .maxOccupancy(e.getMaxOccupancy())
                .standardOccupancy(e.getStandardOccupancy())
                .extraPersonFee(e.getExtraPersonFee())
                .bedInfo(e.getBedInfo())
                .amenities(e.getAmenities())
                .description(e.getDescription())
                .totalRooms(e.getTotalRooms())
                .status(e.getStatus())
                .build();

        // 이미지 조회해서 dto 세팅하기
        List<RoomImageDTO> images = roomImageRepository.findByRoomRoomIdOrderBySortOrderAsc(e.getRoomId())
                .stream().map(this::toImageDTO).collect(Collectors.toList());

        dto.setImages(images);
        return dto;
    }
    private RoomImageDTO toImageDTO(RoomImage entity) {
        return RoomImageDTO.builder()
                .roomId(entity.getRoom().getRoomId())
                .imageUrl(entity.getImageUrl())
                .sortOrder(entity.getSortOrder())
//                .imageType("MAIN") // 우리에겐 imageType이 없음.
                .build();
    }

    @Override
    @Transactional
    public RoomDTO getByRoomId(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("객실이 존재하지 않습니다."));
        return toDTO(room);
    }

    @Override
    public RoomDTO registerRoom(RoomDTO dto) {
        if (dto.getAccommodationId() == null) {
            throw new IllegalArgumentException("accommodationId 는 필수입니다.");
        }
        Accommodation acc = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new EntityNotFoundException("숙소가 존재하지 않습니다."));

        // DTO -> Entity는 빌더로 수동 생성 (연관관계 명시적으로 주입)
        Room room = Room.builder()
                .accommodation(acc)
                .name(dto.getRoomName())
                .type(dto.getType())
                .price(dto.getPrice())
                .maxOccupancy(dto.getMaxOccupancy())
                .standardOccupancy(dto.getStandardOccupancy())
                .extraPersonFee(dto.getExtraPersonFee())
                .bedInfo(jsonNodeToString(stringToJsonNode(dto.getBedInfo())))   // ★ 추가: bedInfo도 변환
                .amenities(jsonNodeToString(stringToJsonNode(dto.getAmenities())))
                .description(dto.getDescription())
                .totalRooms(dto.getTotalRooms())
                .status(dto.getStatus() == null ? RoomStatus.ACTIVE : dto.getStatus())
                .build();
        RoomDTO saved = toDTO(roomRepository.save(room));
        searchCacheInvalidator.bumpAfterCommit(); // 새 객실 = 새 재고
        // 사용자 숙소 상세(객실 목록 포함) 캐시도 커밋 후 지운다
        searchCacheInvalidator.evictAccommodationAfterCommit(acc.getAccommodationId());
        return saved;
    }

    @Override
    public void modifyRoom(RoomDTO dto) {
        if (dto.getRoomId() == null) {
            throw new IllegalArgumentException("roomId 는 필수입니다.");
        }
        Room current = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("객실이 존재하지 않습니다."));
        RoomSearchSnapshot before = RoomSearchSnapshot.of(current); // save(merge) 전에 읽어 둔다

        // 관리 중인 Room(이미지 컬렉션 포함)에서 시작해 들어온 값만 덮어쓴다
        Room.RoomBuilder b = RoomEntityCopy.of(current);
        if (dto.getAccommodationId() != null) b.accommodation(accommodationRepository.getReferenceById(dto.getAccommodationId()));
        if (dto.getRoomName() != null) b.name(dto.getRoomName());
        if (dto.getType() != null) b.type(dto.getType());
        if (dto.getPrice() != null) b.price(dto.getPrice());
        if (dto.getMaxOccupancy() != null) b.maxOccupancy(dto.getMaxOccupancy());
        if (dto.getStandardOccupancy() != null) b.standardOccupancy(dto.getStandardOccupancy());
        if (dto.getExtraPersonFee() != null) b.extraPersonFee(dto.getExtraPersonFee());
        if (dto.getBedInfo() != null) b.bedInfo(jsonNodeToString(stringToJsonNode(dto.getBedInfo())));
        if (dto.getAmenities() != null) b.amenities(jsonNodeToString(stringToJsonNode(dto.getAmenities())));
        if (dto.getDescription() != null) b.description(dto.getDescription());
        if (dto.getTotalRooms() != null) b.totalRooms(dto.getTotalRooms());
        if (dto.getStatus() != null) b.status(dto.getStatus());
        Room updated = b.build();

        roomRepository.save(updated);
        if (RoomSearchSnapshot.of(updated).differsFrom(before)) {
            searchCacheInvalidator.bumpAfterCommit();
        }
        // 상세 화면 캐시는 항상 지운다(숙소 이동 시 양쪽)
        searchCacheInvalidator.evictAccommodationAfterCommit(before.accommodationId());
        searchCacheInvalidator.evictAccommodationAfterCommit(RoomSearchSnapshot.of(updated).accommodationId());
    }

    @Override
    public void removeRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("객실이 존재하지 않습니다."));
        // 예약이 있으면 지우지 않는다. 없으면 FK 때문에 이미지·요금 캘린더 행을 먼저 지운다
        if (reservationRepository.existsByRoom_RoomId(id)) {
            throw new IllegalStateException("해당 객실에 예약 이력이 있어 삭제할 수 없습니다. 대신 객실 상태를 'INACTIVE'로 변경하세요.");
        }
        roomImageRepository.deleteByRoomRoomId(id);
        priceCalendarRepository.deleteByRoomId(id);
        roomRepository.delete(room);
       searchCacheInvalidator.bumpAfterCommit();
       searchCacheInvalidator.evictAccommodationAfterCommit(accommodationIdOf(room));
    }

    // 회사 전체/ 특정 숙소 커버 동시에 페이징
    @Override
    public Page<RoomDTO> findPageByCompany(Integer companyId, Long accommodationId, Pageable pageable) {
        return roomRepository.findRoomsOfCompany(companyId,accommodationId,pageable).map(this::toDTO);
    }

    @Override
    public Page<RoomDTO> findPageByAccommodationId(Long accommodationId, Pageable pageable) {
        return roomRepository.findByAccommodation_AccommodationId(accommodationId, pageable).map(this::toDTO);
    }
    @Override
    public List<RoomDTO> findAllByCompanyId(Integer companyId){
        return roomRepository.findAllByAccommodation_Company_CompanyId(companyId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> getRoomsByAccommodationId(Long accommodationId) {
        return roomRepository.findByAccommodation_AccommodationId(accommodationId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoomDTO findDetailById(Long roomId) {
        Room r = roomRepository.findByIdWithAccommodation(roomId)
                .orElse(null);
        if (r == null) return null; // 또는 예외 던지기

        Accommodation a = r.getAccommodation();

        List<RoomImageDTO> imgs = roomImageRepository
                .findByRoomRoomIdOrderBySortOrderAsc(roomId)
                .stream()
                .map(img -> RoomImageDTO.builder()
                        .roomId(roomId)
                        .imageUrl(img.getImageUrl())
                        .sortOrder(img.getSortOrder())
                        .build())
                .collect(Collectors.toList());

        return RoomDTO.builder()
                .roomId(r.getRoomId())
                .accommodationId(r.getAccommodation().getAccommodationId())
                .accommodationName(r.getAccommodation().getName())
                .roomName(r.getName())
                .type(r.getType())
                .price(r.getPrice())
                .maxOccupancy(r.getMaxOccupancy())
                .extraPersonFee(r.getExtraPersonFee())
                .standardOccupancy(r.getStandardOccupancy())
                .bedInfo(r.getBedInfo())
                .amenities(r.getAmenities())
                .description(r.getDescription())
                .totalRooms(r.getTotalRooms())
                .status(r.getStatus())
                .images(imgs)

                // 숙소 기준 체크인/체크아웃 -> 객실 상세 페이지 표시용
                .displayCheckInTime(a != null ? a.getCheckInTime() : null)
                .displayCheckOutTime(a != null ? a.getCheckOutTime() : null)
                .build();
    }

    @Override
    public void deactivateRoom(Long roomId) { // 객실 삭제 말고 비활성화 하기
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("객실이 존재하지 않습니다."));
        // 상태만 INACTIVE 로 변경 (나머지 값·이미지 컬렉션은 그대로)
        Room updated = RoomEntityCopy.of(room).status(RoomStatus.INACTIVE).build();

        boolean wasActive = !RoomStatus.INACTIVE.equals(room.getStatus());
        roomRepository.save(updated);
        if (wasActive) {
            searchCacheInvalidator.bumpAfterCommit();
            searchCacheInvalidator.evictAccommodationAfterCommit(accommodationIdOf(room));
        }
    }

    @Override
    public void toggleStatus(Long roomId) {
        Long accommodationId = roomRepository.findById(roomId).map(RoomServiceImpl::accommodationIdOf).orElse(null);
        if (roomRepository.toggleStatus(roomId) > 0) {
            searchCacheInvalidator.bumpAfterCommit(); // ACTIVE ↔ INACTIVE: 검색 노출 여부가 바뀜
            searchCacheInvalidator.evictAccommodationAfterCommit(accommodationId);
        }
    }

    private static Long accommodationIdOf(Room room) {
        return room.getAccommodation() != null ? room.getAccommodation().getAccommodationId() : null;
    }
}