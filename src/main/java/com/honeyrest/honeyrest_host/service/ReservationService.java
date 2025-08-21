package com.honeyrest.honeyrest_host.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.repository.AccommodationRepository;
import com.honeyrest.honeyrest_host.repository.ReservationRepository;
import com.honeyrest.honeyrest_host.repository.RoomRepository;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;


    private Reservation toEntity(ReservationDTO dto) {
        return Reservation.builder()
                .reservationId(dto.getReservationId())
                .user(userRepository.getReferenceById(dto.getUserId())) // userId는 DTO에 추가 필요
                .room(roomRepository.getReferenceById(dto.getRoomId())) // roomId는 DTO에 추가 필요
                .accommodationId(accommodationRepository.getReferenceById(dto.getAccommodationId()))
                .accommodationName(dto.getAccommodationName())
                .roomName(dto.getRoomName())
                .reservationNumber(dto.getReservationNumber())
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .guestCount(dto.getGuestCount())
                .guestName(dto.getGuestName())
                .guestPhone(dto.getGuestPhone())
                .price(dto.getPrice())
                .originalPrice(dto.getOriginalPrice())
                .discountAmount(dto.getDiscountAmount())
                .status(dto.getStatus() == null ? "PENDING" : dto.getStatus()) // 기본값
                .cancelReason(dto.getCancelReason())
                .specialRequest(dto.getSpecialRequest())
                .build();
    }


    // Entity -> DTO
    private ReservationDTO toDTO(Reservation reservation) {
        if (reservation == null) return null;

        return ReservationDTO.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getUser().getUserId()) // 이름만 DTO에 담음
                .roomId(reservation.getRoom().getRoomId())
                .accommodationId(reservation.getAccommodationId().getAccommodationId())
                .accommodationName(reservation.getAccommodationName())
                .roomName(reservation.getRoomName())
                .reservationNumber(reservation.getReservationNumber())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .guestCount(reservation.getGuestCount())
                .guestName(reservation.getGuestName())
                .guestPhone(reservation.getGuestPhone())
                .price(reservation.getPrice())
                .originalPrice(reservation.getOriginalPrice())
                .discountAmount(reservation.getDiscountAmount())
                .status(reservation.getStatus())
                .cancelReason(reservation.getCancelReason())
                .specialRequest(reservation.getSpecialRequest())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }

    public ReservationDTO getReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("해당하는 쿠폰이 존재하지 않습니다"));
        return toDTO(reservation);
    }

    public List<ReservationDTO> getReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ReservationDTO> getReservationsByAccommodationId(Long accommodationId) {
        return reservationRepository.findReservationsByAccommodationId_AccommodationId(accommodationId)
                .stream().map(this::toDTO).toList();
    }

    public void registerReservation(ReservationDTO dto) { reservationRepository.save(toEntity(dto));
    }

    public void modifyReservation(ReservationDTO dto) {
        reservationRepository.save(toEntity(dto));
    }

    public void removeReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}
