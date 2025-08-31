package com.honeyrest.honeyrest_host.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoOwner.RoomDTO;
import com.honeyrest.honeyrest_host.dtoOwner.RoomImageDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.entity.RoomImage;
import com.honeyrest.honeyrest_host.repository.AccommodationRepository;
import com.honeyrest.honeyrest_host.repository.RoomImageRepository;
import com.honeyrest.honeyrest_host.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService{
    private final RoomRepository roomRepository;
    private final AccommodationRepository accommodationRepository;
    private final ObjectMapper objectMapper;
    private final RoomImageRepository roomImageRepository;

    private String parseJson(String input) {
        if (input == null || input.isBlank()) return "[]";
        try {
            List<String> amenitiesList = Arrays.stream(input.split("[,\\s]+"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            return objectMapper.writeValueAsString(amenitiesList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]"; // 실패 시 빈 배열 반환
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
                .bedInfo(parseString(e.getBedInfo()))
                .amenities(parseString(e.getAmenities()))
                .description(e.getDescription())
                .totalRooms(e.getTotalRooms())
                .status(e.getStatus())
                .build();
    }

    /**
     * "wifi:true, tv:true" -> Map<String,Object> 변환
     */
    private List<String> parseList(String jsonInput) {
        if (jsonInput == null || jsonInput.isBlank()) return Collections.emptyList();

        try {
            // JSON 배열 문자열을 List<String>으로 역직렬화
            return objectMapper.readValue(jsonInput, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList(); // 실패 시 빈 리스트 반환
        }
    }
    private String parseString(String jsonInput) {
        List<String> list = parseList(jsonInput);
        return String.join(", ", list);
    }
    private Room toEntity(RoomDTO d) {
        String amenitiesJson = "[]"; // 기본값
        String bedInfoJson = "[]"; // 기본값


        if (d.getAmenities() != null && !d.getAmenities().isBlank()) {
            amenitiesJson = (parseJson(d.getAmenities()));
            bedInfoJson = (parseJson(d.getBedInfo()));
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

    private RoomImage toRoomImageEntity(RoomImageDTO d) {
        return RoomImage.builder()
                .imageId(d.getImageId())
                .imageUrl(d.getImageUrl())
                .sortOrder(d.getSortOrder())
                .room(roomRepository.getReferenceById(d.getRoomId()))
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
    public Long registerRoom(RoomDTO dto) {
        Accommodation acc = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));
        dto.setAccommodationId(acc.getAccommodationId());
        return roomRepository.save(toEntity(dto)).getRoomId();

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
    }

    @Override
    public List<RoomDTO> searchByNameContaining(Long accommodationId, String keyword) {
        return roomRepository.findByAccommodation_AccommodationIdAndNameContainingIgnoreCase(accommodationId, keyword)
                .stream().map(this::toDTO).toList();
    }
}
