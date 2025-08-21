package com.honeyrest.honeyrest_host.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dtoOwner.RoomDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repository.AccommodationRepository;
import com.honeyrest.honeyrest_host.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService{
    private final RoomRepository roomRepository;
    private final AccommodationRepository accommodationRepository;
    private final ObjectMapper objectMapper;

    private String parseJson(String json) {
        if (json == null || json.isBlank()) return "";
        try {
            // JSON을 Map으로 변환
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            // "key:value" 형식으로 변환 후 join
            return map.entrySet().stream()
                    .map(e -> e.getKey() + ":" + e.getValue())
                    .collect(Collectors.joining(", "));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return json; // 실패하면 그냥 원본 JSON 반환
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
                .bedInfo(parseJson(e.getBedInfo()))
                .amenities(parseJson(e.getAmenities()))
                .description(e.getDescription())
                .totalRooms(e.getTotalRooms())
                .status(e.getStatus())
                .build();
    }

    /**
     * "wifi:true, tv:true" -> Map<String,Object> 변환
     */
    private Map<String, Object> parseString(String str) {
        Map<String, Object> map = new HashMap<>();
        String[] entries = str.split(",");
        for (String entry : entries) {
            String[] kv = entry.trim().split(":");
            if (kv.length == 2) {
                String key = kv[0].trim();
                String valueStr = kv[1].trim();
                Object value = "true".equalsIgnoreCase(valueStr) ? true :
                        "false".equalsIgnoreCase(valueStr) ? false : valueStr;
                map.put(key, value);
            }
        }
        return map;
    }

    private Room toEntity(RoomDTO d) {
        String amenitiesJson = "[]"; // 기본값
        String bedInfoJson = "[]"; // 기본값


        try {
            if (d.getAmenities() != null && !d.getAmenities().isBlank()) {
                amenitiesJson = objectMapper.writeValueAsString(parseString(d.getAmenities()));
                bedInfoJson = objectMapper.writeValueAsString(parseString(d.getBedInfo()));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // 변환 실패 시 로그 출력
        }
        return Room.builder()
                .roomId(d.getRoomId())
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
                .status(d.getStatus() == null ? "ACTIVE" : d.getStatus())
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
                .orElseThrow(() -> new NotFoundException("객실이 존재하지 않습니다."));
        return toDTO(room);
    }

    @Override
    public void registerRoom(RoomDTO dto) {
        Accommodation acc = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));
        dto.setAccommodationId(acc.getAccommodationId());
        roomRepository.save(toEntity(dto));
    }

    @Override
    public void modifyRoom(RoomDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new NotFoundException("객실이 존재하지 않습니다."));
        roomRepository.save(toEntity(dto));
    }

    @Override
    public void removeRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("객실이 존재하지 않습니다."));
        roomRepository.delete(room); // RoomImage는 orphanRemoval=true로 함께 삭제
    }
}
