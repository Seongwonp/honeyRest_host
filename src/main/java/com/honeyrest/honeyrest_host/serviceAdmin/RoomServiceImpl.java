package com.honeyrest.honeyrest_host.serviceAdmin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dto.RoomImageDTO;
import com.honeyrest.honeyrest_host.entity.RoomImage;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomImageRepository;
import jakarta.persistence.EntityNotFoundException;
import com.honeyrest.honeyrest_host.dto.RoomDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RoomImageRepository roomImageRepository;

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

    private Room toEntity(RoomDTO d) {
        return Room.builder()
                .roomId(d.getRoomId())
                .accommodation(accommodationRepository.getReferenceById(d.getAccommodationId()))
                .name(d.getRoomName())
                .type(d.getType())
                .price(d.getPrice())
                .maxOccupancy(d.getMaxOccupancy())
                .standardOccupancy(d.getStandardOccupancy())
                .extraPersonFee(d.getExtraPersonFee())
                .bedInfo(d.getBedInfo())
                .amenities(d.getAmenities())
                .description(d.getDescription())
                .totalRooms(d.getTotalRooms())
                .status(d.getStatus() == null ? "ACTIVE" : d.getStatus())
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
                .status(dto.getStatus() == null ? "ACTIVE" : dto.getStatus())
                .build();
       return toDTO(roomRepository.save(room));
    }

    @Override
    public void modifyRoom(RoomDTO dto) {
        if (dto.getRoomId() == null) {
            throw new IllegalArgumentException("roomId 는 필수입니다.");
        }
        Room current = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("객실이 존재하지 않습니다."));

        Room updated = Room.builder()
                .roomId(current.getRoomId())
                .accommodation(dto.getAccommodationId() != null
                        ? accommodationRepository.getReferenceById(dto.getAccommodationId())
                        : current.getAccommodation())
                .name(dto.getRoomName() != null ? dto.getRoomName() : current.getName())
                .type(dto.getType() != null ? dto.getType() : current.getType())
                .price(dto.getPrice() != null ? dto.getPrice() : current.getPrice())
                .maxOccupancy(dto.getMaxOccupancy() != null ? dto.getMaxOccupancy() : current.getMaxOccupancy())
                .standardOccupancy(dto.getStandardOccupancy() != null ? dto.getStandardOccupancy() : current.getStandardOccupancy())
                .extraPersonFee(dto.getExtraPersonFee() != null ? dto.getExtraPersonFee() : current.getExtraPersonFee())
                .bedInfo(dto.getBedInfo() != null ? jsonNodeToString(stringToJsonNode(dto.getBedInfo())) : current.getBedInfo())
                .amenities(dto.getAmenities() != null ? jsonNodeToString(stringToJsonNode(dto.getAmenities())) : current.getAmenities())
                .description(dto.getDescription() != null ? dto.getDescription() : current.getDescription())
                .totalRooms(dto.getTotalRooms() != null ? dto.getTotalRooms() : current.getTotalRooms())

                .status(dto.getStatus() != null ? dto.getStatus() : current.getStatus())
                .build();

        roomRepository.save(updated);
    }

    @Override
    public void removeRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("객실이 존재하지 않습니다."));
        // 실제 삭제 대신 상태만 변경
       roomRepository.delete(room);
    }

    // 회사 전체/ 특정 숙소 커버 동시에 페이징
    @Override
    public Page<RoomDTO> findPageByCompany(Long companyId, Long accommodationId, Pageable pageable) {
        return roomRepository.findRoomsOfCompany(companyId,accommodationId,pageable).map(this::toDTO);
    }

    @Override
    public Page<RoomDTO> findPageByAccommodationId(Long accommodationId, Pageable pageable) {
        return roomRepository.findByAccommodation_AccommodationId(accommodationId, pageable).map(this::toDTO);
    }
    @Override
    public List<RoomDTO> findAllByCompanyId(Long companyId){
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
        // 상태만 INACTIVE 로 변경
        Room updated = Room.builder()
                .roomId(room.getRoomId())
                .accommodation(room.getAccommodation())
                .name(room.getName())
                .type(room.getType())
                .price(room.getPrice())
                .maxOccupancy(room.getMaxOccupancy())
                .standardOccupancy(room.getStandardOccupancy())
                .extraPersonFee(room.getExtraPersonFee())
                .bedInfo(room.getBedInfo())
                .amenities(room.getAmenities())
                .description(room.getDescription())
                .totalRooms(room.getTotalRooms())
                .status("INACTIVE")   // ★ 비활성화
                .build();

        roomRepository.save(updated);
    }

    @Override
    public void toggleStatus(Long roomId) {
        roomRepository.toggleStatus(roomId);
    }
}