package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dtoOwner.PriceCalendarDTO;
import com.honeyrest.honeyrest_host.entity.PriceCalendar;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repository.PriceCalendarRepository;
import com.honeyrest.honeyrest_host.repository.ReservationRepository;
import com.honeyrest.honeyrest_host.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PriceCalendarService {
    private final PriceCalendarRepository priceCalendarRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    private PriceCalendarDTO toDTO(PriceCalendar entity) {
        return PriceCalendarDTO.builder()
                .date(entity.getDate())
                .price(entity.getPrice())
                .availableRoom(entity.getAvailableRoom())
                .calendarId(entity.getCalendarId())
                .roomId(entity.getRoom().getRoomId())
                .build();
    }

    private PriceCalendar toEntity(PriceCalendarDTO dto) {
        return PriceCalendar.builder()
                .calendarId(dto.getCalendarId())
                .date(dto.getDate())
                .price(dto.getPrice())
                .availableRoom(dto.getAvailableRoom())
                .room(roomRepository.getReferenceById(dto.getRoomId()))
                .build();
    }

    public List<PriceCalendarDTO> getPriceCalendars(Long roomId, LocalDate startDate, LocalDate endDate) {
        return priceCalendarRepository.findByRoom_RoomIdAndDateBetween(roomId, startDate, endDate)
                .stream().map(this::toDTO).toList();
    }
}
