package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.entity.enums.ReservationStatus;
import com.honeyrest.honeyrest_host.repository.ReservationRepository;
import com.honeyrest.honeyrest_host.repository.RoomRepository;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageResponseDTO<ReservationDTO> getReservationsByStatus(String status, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable(Sort.by("reservationId").descending());

        Page<Reservation> page;
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            page = reservationRepository.findAll(pageable);
        } else {
            ReservationStatus st;
            try {
                st = ReservationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("유효하지 않은 예약 상태: " + status);
            }
            page = reservationRepository.findByStatus(st, pageable);
        }

        List<ReservationDTO> dtoList = page.getContent().stream()
                .map(r -> modelMapper.map(r, ReservationDTO.class))
                .toList();

        return PageResponseDTO.<ReservationDTO>withALl()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) page.getTotalElements())
                .build();
    }

    @Override
    public ReservationDTO getReservationByNumber(String number) {
        Reservation r = reservationRepository.findByReservationNumber(number)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. 예약번호=" + number));
        return modelMapper.map(r, ReservationDTO.class);
    }

    @Override
    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));
    }

    // ✅ 수정: 세터 대신 도메인 메서드 사용
    @Override
    public Reservation updateReservation(ReservationDTO dto) {
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + dto.getReservationId()));

        User newUser = null;
        Room newRoom = null;

        if (dto.getUserId() != null) {
            newUser = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다. userId=" + dto.getUserId()));
        }
        if (dto.getRoomId() != null) {
            newRoom = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new EntityNotFoundException("객실을 찾을 수 없습니다. roomId=" + dto.getRoomId()));
        }

        reservation.update(dto, newUser, newRoom); // 변경감지

        return reservation; // @Transactional 이므로 save() 필요 없음
    }

    // ✅ 취소
    @Override
    public void canceledReservation(Long reservationId, String cancelReason) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));
        r.cancel(cancelReason); // 도메인 메서드 사용
    }

    // ✅ 생성
    @Override
    public ReservationDTO createReservation(ReservationDTO dto) {
        dto.setReservationId(null);

        if (dto.getRoomId() == null) {
            throw new IllegalStateException("roomId는 필수입니다.");
        }
        if (dto.getUserId() == null) {
            throw new IllegalStateException("userId는 필수입니다.");
        }
        if (dto.getCheckInDate() != null && dto.getCheckOutDate() != null
                && !dto.getCheckInDate().isBefore(dto.getCheckOutDate())) {
            throw new IllegalArgumentException("체크인 날짜는 체크아웃보다 이전이어야 합니다.");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다. userId=" + dto.getUserId()));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("객실을 찾을 수 없습니다. roomId=" + dto.getRoomId()));

        // 필요 시 room에서 숙소 정보 끌어오기
        Long accId = dto.getAccommodationId() != null ? dto.getAccommodationId()
                : room.getAccommodation().getAccommodationId();
        String accName = dto.getAccommodationName() != null ? dto.getAccommodationName()
                : room.getAccommodation().getName();
        String roomName = room.getName(); // 엔티티 필드명에 맞게

        String reservationNumber = (dto.getReservationNumber() != null && !dto.getReservationNumber().isBlank())
                ? dto.getReservationNumber()
                : genReservationNumber();

        Reservation newEntity = Reservation.builder()
                .user(user)
                .room(room)
                .accommodationId(accId)
                .accommodationName(accName)
                .roomName(roomName)
                .reservationNumber(reservationNumber)
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .guestCount(dto.getGuestCount())
                .guestName(dto.getGuestName())
                .guestPhone(dto.getGuestPhone())
                .price(dto.getPrice())
                .originalPrice(dto.getPrice())  // 필요 시 계산 로직 적용
                .discountAmount(dto.getPrice() != null ? BigDecimal.ZERO : null)
                .status(dto.getStatus() != null ? dto.getStatus() : ReservationStatus.PENDING)
                .cancelReason(dto.getCancelReason())
                .specialRequest(dto.getSpecialRequest())
                .build();

        newEntity.validateNew(); // 엔티티 자체 검증

        Reservation saved = reservationRepository.save(newEntity);
        return modelMapper.map(saved, ReservationDTO.class);
    }

    // 간단한 예약번호 생성(원하면 바꿔도 됨)
    private String genReservationNumber() {
        // 예: HR-20240818-랜덤6자리
        String rand = Long.toString(System.nanoTime(), 36).toUpperCase();
        return "HR-" + java.time.LocalDate.now() + "-" + rand.substring(Math.max(0, rand.length() - 6));
    }

    @Override
    public PageResponseDTO<ReservationDTO> getAllReservations(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable(Sort.by("reservationId").descending());

        Page<Reservation> page = reservationRepository.findAll(pageable);

        List<ReservationDTO> dtoList = page.getContent().stream()
                .map(r -> modelMapper.map(r, ReservationDTO.class))
                .toList();

        return PageResponseDTO.<ReservationDTO>withALl()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) page.getTotalElements())
                .build();
    }

    @Override
    public List<ReservationDTO> getAllReservationsNoPaging() {
        return reservationRepository.findAll().stream()
                .map(r -> modelMapper.map(r, ReservationDTO.class))
                .toList();
    }
}