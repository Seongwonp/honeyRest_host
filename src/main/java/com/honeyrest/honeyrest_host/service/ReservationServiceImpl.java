package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.entity.enums.ReservationStatus;
import com.honeyrest.honeyrest_host.repository.ReservationRepository;
import com.honeyrest.honeyrest_host.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    @Override
    // 예약 현황으로 목록 조회
    public PageResponseDTO<ReservationDTO> getReservationsByStatus(String status, PageRequestDTO pageRequestDTO) {
        Sort sortSpec = Sort.by("reservationId").descending();
        Pageable pageable = pageRequestDTO.getPageable(sortSpec);

        // 예약 상태가 비었거나, all 이면 전체 조회
        Page<Reservation> page = (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status))
                ? reservationRepository.findAll(pageable)
                : reservationRepository.findByStatus(status, pageable);

        // 엔티티 -> dto 변환
        List<ReservationDTO> dtoList = page.getContent().stream()
                .map(res -> modelMapper.map(res, ReservationDTO.class))
                .toList();

        // dto 조립
        return PageResponseDTO.<ReservationDTO>withALl()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) page.getTotalElements())
                .build();

    }

    @Override
    // 예약 번호로 조회
    public ReservationDTO getReservationByNumber(String number) {
        Reservation reservation = reservationRepository.findByReservationNumber(number)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. 예약 번호 = " + number));
        return modelMapper.map(reservation, ReservationDTO.class);
    }

    @Override
    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));
    }

    @Override
    public Reservation updateReservation(ReservationDTO reservationDTO) {
        // 엔티티 조회
        Reservation reservation = reservationRepository.findById(reservationDTO.getReservationId()).orElseThrow(()
                -> new EntityNotFoundException("예약을 찾을 수 없습니다 id=" + reservationDTO.getReservationId()));


        if (reservationDTO.getRoomId() != null) {
            Room room = roomRepository.findById(reservationDTO.getRoomId()).orElseThrow(() -> new EntityNotFoundException("객실을 찾을 수 없습니다 roomid=" + reservationDTO.getRoomId()));

            // 필드 접근(setter 없이)
            setField(reservation, "room", room);


        }

        ReservationDTO patch = ReservationDTO.builder()
                .checkInDate(reservationDTO.getCheckInDate())
                .checkOutDate(reservationDTO.getCheckOutDate())
                .guestCount(reservationDTO.getGuestCount())
                .guestPhone(reservationDTO.getGuestPhone())
                .guestName(reservationDTO.getGuestName())
                .status(reservationDTO.getStatus())
                .cancelReason(reservationDTO.getCancelReason())
                .specialRequest(reservationDTO.getSpecialRequest())
                .userId(reservationDTO.getUserId())
                .roomId(reservationDTO.getRoomId())
                .reservationNumber(reservationDTO.getReservationNumber())
                .accommodationId(reservationDTO.getAccommodationId())
                .accommodationName(reservationDTO.getAccommodationName())
                .price(reservationDTO.getPrice())
                .build();

        return reservationRepository.save(modelMapper.map(patch, Reservation.class));
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("필드 설정 실패: " + fieldName, e);
        }

    }


    @Override
    public void canceledReservation(Long reservationId, String cancelReason) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));

        // 이미 예약 취소 검사
        if (r.getStatus() == ReservationStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 예약입니다. id=" + reservationId);
        }

        ReservationDTO reservationDTO = ReservationDTO.builder()
                .status(ReservationStatus.CANCELED)
                .cancelReason(cancelReason)
                .build();

        modelMapper.map(reservationDTO, r);
    }

    @Override
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        if (reservationDTO.getRoomId() == null || reservationDTO.getRoomId() <= 0) {
            throw new IllegalStateException("roomId는 필수 입니다..");
        }

        if (reservationDTO.getCheckInDate() != null && reservationDTO.getCheckOutDate() != null) {
            if (!reservationDTO.getCheckInDate().isBefore(reservationDTO.getCheckOutDate())) {
                throw new IllegalArgumentException("체크인 날짜는 체크아웃 날짜보다 이전이어야 합니다.>");
            }
        }

        // 존재 검증 (여기서 실패하면 "객실을 찾을 수 없습니다" 예외가 나와야 정상)
        Room room = roomRepository.findById(reservationDTO.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("객실을 찾을 수 없습니다. roomId=" + reservationDTO.getRoomId()));

        Reservation entity = modelMapper.map(reservationDTO, Reservation.class);
        setField(entity, "room", room);
        // ...
        Reservation saved = reservationRepository.save(entity);
        return modelMapper.map(saved, ReservationDTO.class);
    }
}


