package com.honeyrest.honeyrest_host.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.honeyrest.honeyrest_host.dto.RoomDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repository.AccommodationRepository;
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
                .status(d.getStatus() == null ? "ACTIVE" : d.getStatus())
                .build();
    }

    @Override
    public List<RoomDTO> findRoomsByAccommodationId(Long accommodationId) {
        return roomRepository.findByAccommodation_AccommodationId(accommodationId)
                .stream().map(room -> modelMapper.map(room, RoomDTO.class)).toList();
    }

    @Override
    public RoomDTO getByRoomId(Long id) {
        return roomRepository.findById(id).map(room -> modelMapper.map(room, RoomDTO.class))
                .orElseThrow(() -> new NotFoundException("객실이 존재하지 않습니다."));
    }

    @Override
    public Long registerRoom(RoomDTO dto) {
        Accommodation acc = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));
        dto.setAccommodationId(acc.getAccommodationId());
        Room room = modelMapper.map(dto, Room.class);
        roomRepository.save(room);
        return room.getRoomId();
    }

    @Override
    public void modifyRoom(RoomDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new NotFoundException("객실이 존재하지 않습니다."));
//        // 숙소 변경도 허용하려면
//        if (dto.getAccommodationId() != null && !dto.getAccommodationId().equals(room.getAccommodation().getAccommodationId())) {
//            Accommodation acc = accommodationRepository.findById(dto.getAccommodationId())
//                    .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));
//            room.setAccommodation(acc);
//        }
        modelMapper.map(dto, room);
        roomRepository.save(room);
    }

    @Override
    public void removeRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("객실이 존재하지 않습니다."));
        roomRepository.delete(room); // RoomImage는 orphanRemoval=true로 함께 삭제
    }
}