package com.honeyrest.honeyrest_host.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.entity.enums.OperationStatus;
import com.honeyrest.honeyrest_host.entity.enums.RoomType;
import jakarta.persistence.EntityNotFoundException;
import com.honeyrest.honeyrest_host.dto.RoomDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService{
    private final RoomRepository roomRepository;
    private final AccommodationRepository accommodationRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
            try { return objectMapper.readTree("[]"); } catch (Exception ignored) { return null; }
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
        return RoomDTO.builder()
                .roomId(e.getRoomId())
                .accommodationId(e.getAccommodation().getAccommodationId())
                .name(e.getName())
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
    }

    private Room toEntity(RoomDTO d) {
        return Room.builder()
                .roomId(d.getRoomId())
                .accommodation(accommodationRepository.getReferenceById(d.getAccommodationId()))
                .name(d.getName())
                .type(d.getType())
                .price(d.getPrice())
                .maxOccupancy(d.getMaxOccupancy())
                .standardOccupancy(d.getStandardOccupancy())
                .extraPersonFee(d.getExtraPersonFee())
                .bedInfo(d.getBedInfo())
                .amenities(d.getAmenities())
                .description(d.getDescription())
                .totalRooms(d.getTotalRooms())
                .status(d.getStatus() == null ? OperationStatus.ACTIVE : d.getStatus())
                .build();
    }

    @Override
    public List<RoomDTO> findRoomsByAccommodationId(Long accommodationId) {
        return roomRepository.findByAccommodation_AccommodationId(accommodationId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
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
                .name(dto.getName())
                .type(dto.getType())
                .price(dto.getPrice())
                .maxOccupancy(dto.getMaxOccupancy())
                .standardOccupancy(dto.getStandardOccupancy())
                .extraPersonFee(dto.getExtraPersonFee())
                .bedInfo(jsonNodeToString(stringToJsonNode(dto.getBedInfo())))   // ★ 추가: bedInfo도 변환
                .amenities(jsonNodeToString(stringToJsonNode(dto.getAmenities())))
                .description(dto.getDescription())
                .totalRooms(dto.getTotalRooms())
                .status(dto.getStatus() == null ? OperationStatus.ACTIVE : dto.getStatus())
                .build();
        Room saved = roomRepository.save(room);
        return toDTO(saved);
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
                .name(dto.getName() != null ? dto.getName() : current.getName())
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
        roomRepository.delete(room);
    }
}