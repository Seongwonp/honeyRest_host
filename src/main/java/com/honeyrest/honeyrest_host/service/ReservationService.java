package com.honeyrest.honeyrest_host.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;

    public ReservationDTO getReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("해당하는 쿠폰이 존재하지 않습니다"));
        return modelMapper.map(reservation, ReservationDTO.class);
    }

    public List<ReservationDTO> getReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(reservation -> modelMapper.map(reservation, ReservationDTO.class))
                .toList();
    }

    public void registerReservation(ReservationDTO dto) {

        reservationRepository.save(modelMapper.map(dto, Reservation.class));
    }

    public void modifyReservation(ReservationDTO dto) {
        reservationRepository.save(modelMapper.map(dto, Reservation.class));
    }

    public void removeReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}
