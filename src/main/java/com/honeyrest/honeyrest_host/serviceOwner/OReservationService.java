package com.honeyrest.honeyrest_host.serviceOwner;

import com.amazonaws.services.kms.model.NotFoundException;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PriceCalendarDTO;
import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OReservationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OReservationService {
    private final OReservationRepository reservationRepository;
    private final ORoomRepository roomRepository;
    private final OUserRepository userRepository;
    private final OAccommodationRepository accommodationRepository;


    private Reservation toEntity(ReservationDTO dto) {
        return Reservation.builder()
                .reservationId(dto.getReservationId())
                .user(userRepository.getReferenceById(dto.getUserId())) // userId는 DTO에 추가 필요
                .room(roomRepository.getReferenceById(dto.getRoomId())) // roomId는 DTO에 추가 필요
                .accommodation(accommodationRepository.getReferenceById(dto.getAccommodationId()))
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
                .accommodationId(reservation.getAccommodation().getAccommodationId())
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

    public List<ReservationDTO> getReservationsByActive() {
        return reservationRepository.findAll()
                .stream()
                .filter(reservation -> !"CANCEL".equalsIgnoreCase(reservation.getStatus()))
                .map(this::toDTO)
                .toList();
    }

    public List<ReservationDTO> getReservationsByAccommodationId(Long accommodationId) {
        return reservationRepository.findReservationsByAccommodation_AccommodationId(accommodationId)
                .stream().map(this::toDTO).toList();
    }

    public List<ReservationDTO> getReservationsByCompanyId(Long companyId) {
        return reservationRepository.findReservationsByAccommodation_Company_CompanyId(companyId)
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

    public Map<LocalDate, PriceCalendarDTO> getCalendarData(Long roomId, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, PriceCalendarDTO> calendarMap = new HashMap<>();

        // 1. 방(Room) 조회
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Room ID: " + roomId));

        // 2. 해당 기간 예약 조회
        List<Reservation> reservations = reservationRepository.findByRoomIdAndDateBetween(roomId, startDate, endDate);

        // 3. 날짜별 처리
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDate finalDate = date;

            // 해당 날짜에 걸려 있는 예약 리스트 필터링
            List<Reservation> reservationsOnDate = reservations.stream()
                    .filter(r -> !r.getCheckInDate().isAfter(finalDate) && r.getCheckOutDate().isAfter(finalDate))
                    .filter(r -> !r.getStatus().equalsIgnoreCase("cancel"))
                    .toList();

            // 예약 수 계산
            int reservedCount = reservationsOnDate.size();

            // 예약된 가격 합산
            BigDecimal totalPrice = reservationsOnDate.stream()
                    .map(Reservation::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int availableRooms = room.getTotalRooms() - reservedCount;

            PriceCalendarDTO dto = PriceCalendarDTO.builder()
                    .roomId(room.getRoomId())
                    .date(date)
                    .price(totalPrice)  // 여기 수정됨
                    .availableRoom(Math.max(availableRooms, 0))
                    .build();

            calendarMap.put(date, dto);
        }

        return calendarMap;
    }

    public PageResponseDTO<ReservationDTO> getReservationsByCompanyIdWithPageable(Long companyId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("reservationId").descending());
        Page<Reservation> page;
        if (companyId != null && companyId > 0) {
            page = reservationRepository.findByAccommodation_Company_CompanyId(companyId, pageable);
        } else {
            page = reservationRepository.findAll(pageable);
        }

        List<ReservationDTO> list = page.getContent().stream()
                .map(this::toDTO)
                .filter(reservation ->
                        !reservation.getStatus().equalsIgnoreCase("cancel") &&
                                !reservation.getStatus().equalsIgnoreCase("cancel_request")
                )
                .toList();

        long total = page.getTotalElements();

        return PageResponseDTO.<ReservationDTO>withAll()
                .dtoList(list)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    public PageResponseDTO<ReservationDTO> getCancelRequestReservationsByCompanyIdWithPageable(Long companyId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("reservationId").descending());
        Page<Reservation> page;
        if (companyId != null && companyId > 0) {
            page = reservationRepository.findByAccommodation_Company_CompanyId(companyId, pageable);
        } else {
            page = reservationRepository.findAll(pageable);
        }

        List<ReservationDTO> list = page.getContent().stream()
                .map(this::toDTO)
                .filter(reservation -> reservation.getStatus().equalsIgnoreCase("cancel_request"))
                .toList();

        long total = list.size();

        return PageResponseDTO.<ReservationDTO>withAll()
                .dtoList(list)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    public PageResponseDTO<ReservationDTO> getReservationsByAccommodationIdWithPageable(Long accommodationId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("reservationId").descending());
        Page<Reservation> page;
        if (accommodationId != null && accommodationId > 0) {
            page = reservationRepository.findByAccommodation_AccommodationId(accommodationId, pageable);
        } else {
            page = reservationRepository.findAll(pageable);
        }

        List<ReservationDTO> list = page.getContent().stream()
                .map(this::toDTO)
                .filter(reservation ->
                        !reservation.getStatus().equalsIgnoreCase("cancel") &&
                                !reservation.getStatus().equalsIgnoreCase("cancel_request")
                )
                .toList();

        long total = page.getTotalElements();

        return PageResponseDTO.<ReservationDTO>withAll()
                .dtoList(list)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }
    public PageResponseDTO<ReservationDTO> getCancelRequestReservationsByAccommodationIdWithPageable(Long accommodationId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("reservationId").descending());
        Page<Reservation> page;
        if (accommodationId != null && accommodationId > 0) {
            page = reservationRepository.findByAccommodation_AccommodationId(accommodationId, pageable);
        } else {
            page = reservationRepository.findAll(pageable);
        }

        List<ReservationDTO> list = page.getContent().stream()
                .map(this::toDTO)
                .filter(reservation -> reservation.getStatus().equalsIgnoreCase("cancel_request")
                )
                .toList();

        long total = page.getTotalElements();

        return PageResponseDTO.<ReservationDTO>withAll()
                .dtoList(list)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    public PageResponseDTO<ReservationDTO> getReservationsByRoomIdWithPage(Long roomId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("reservationId").descending());

        Page<Reservation> page = reservationRepository.findByRoom_RoomId(roomId, pageable);

        List<ReservationDTO> list = page.getContent().stream()
                .map(this::toDTO)
                .toList();

        long total = page.getTotalElements();

        return PageResponseDTO.<ReservationDTO>withAll()
                .dtoList(list)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    public List<ReservationDTO> getReservations(Long roomId, LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = reservationRepository.findByRoomIdAndDateRange(roomId, startDate, endDate);
        return reservations.stream()
                .map(r -> ReservationDTO.builder()
                        .reservationId(r.getReservationId())
                        .roomId(r.getRoom().getRoomId())
                        .roomName(r.getRoomName())
                        .guestName(r.getGuestName())
                        .checkInDate(r.getCheckInDate())
                        .checkOutDate(r.getCheckOutDate())
                        .price(r.getPrice())
                        .status(r.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

}
