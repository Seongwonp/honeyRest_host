package com.honeyrest.honeyrest_host.serviceOwner;

import jakarta.persistence.EntityNotFoundException;
import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PriceCalendarDTO;
import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.entity.ReservationStatus;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OReservationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationInventoryGuard;
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
    private final ReservationInventoryGuard inventoryGuard;
    // 예약 점유가 바뀌면 사용자 API 검색 캐시 세대를 커밋 후 올린다
    private final SearchCacheInvalidator searchCacheInvalidator;


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
                .status(ReservationStatus.normalize(dto.getStatus(), ReservationStatus.PENDING)) // 기본값 PENDING
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
                .orElseThrow(()-> new EntityNotFoundException("해당 예약이 존재하지 않습니다. id=" + id));
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
                .filter(reservation -> !ReservationStatus.CANCELLED.equalsIgnoreCase(reservation.getStatus()))
                .map(this::toDTO)
                .toList();
    }

    public List<ReservationDTO> getReservationsByAccommodationId(Long accommodationId) {
        return reservationRepository.findReservationsByAccommodation_AccommodationId(accommodationId)
                .stream().map(this::toDTO).toList();
    }

    public List<ReservationDTO> getReservationsByCompanyId(Integer companyId) {
        return reservationRepository.findReservationsByAccommodation_Company_CompanyId(companyId)
                .stream().map(this::toDTO).toList();
    }


    /**
     * 예약 등록 (기본 상태 PENDING).
     * 점유 상태면 객실 행을 잠그고 겹치는 점유 예약 수가 totalRooms 이상이면 ReservationConflictException.
     */
    public void registerReservation(ReservationDTO dto) {
        dto.setReservationId(null);
        String status = ReservationStatus.normalize(dto.getStatus(), ReservationStatus.PENDING);
        dto.setStatus(status);
        if (ReservationStatus.isOccupying(status)) {
            inventoryGuard.lockRoomAndAssertAvailable(dto.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate(), null);
        }
        reservationRepository.save(toEntity(dto));
        if (ReservationStatus.isOccupying(status)) {
            searchCacheInvalidator.bumpAfterCommit();
        }
    }

    /**
     * 예약 수정. 비점유 → 점유 상태 전환(예: CANCELLED → CONFIRMED)이거나
     * 점유 상태에서 객실/기간이 바뀌면 등록과 같은 재고 검사를 한다(자기 자신은 제외).
     */
    public void modifyReservation(ReservationDTO dto) {
        Reservation existing = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 예약이 존재하지 않습니다. id=" + dto.getReservationId()));
        String newStatus = ReservationStatus.normalize(dto.getStatus(), existing.getStatus());
        dto.setStatus(newStatus);

        Long oldRoomId = existing.getRoom() != null ? existing.getRoom().getRoomId() : null;
        boolean enteringOccupying = ReservationStatus.isOccupying(newStatus)
                && !ReservationStatus.isOccupying(existing.getStatus());
        boolean occupyingChanged = ReservationStatus.isOccupying(newStatus)
                && (!java.util.Objects.equals(oldRoomId, dto.getRoomId())
                    || !java.util.Objects.equals(existing.getCheckInDate(), dto.getCheckInDate())
                    || !java.util.Objects.equals(existing.getCheckOutDate(), dto.getCheckOutDate()));
        if (enteringOccupying || occupyingChanged) {
            inventoryGuard.lockRoomAndAssertAvailable(dto.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate(),
                    existing.getReservationId());
        }
        boolean leavingOccupying = ReservationStatus.isOccupying(existing.getStatus())
                && !ReservationStatus.isOccupying(newStatus);
        reservationRepository.save(toEntity(dto));
        if (enteringOccupying || leavingOccupying || occupyingChanged) {
            searchCacheInvalidator.bumpAfterCommit();
        }
    }

    public void removeReservation(Long id) {
        boolean wasOccupying = reservationRepository.findById(id)
                .map(r -> ReservationStatus.isOccupying(r.getStatus()))
                .orElse(false);
        reservationRepository.deleteById(id);
        if (wasOccupying) {
            searchCacheInvalidator.bumpAfterCommit();
        }
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
                    .filter(r -> ReservationStatus.isOccupying(r.getStatus())) // 재고 점유 상태만
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

    /** 오너 "활성" 예약 목록에서 제외하는 상태 (취소 완료 + 취소 요청은 별도 화면에서 처리) */
    static final List<String> INACTIVE_STATUSES = List.of(ReservationStatus.CANCELLED, ReservationStatus.CANCEL_REQUEST);
    /** 오너 취소 요청 화면에 보여줄 상태 */
    static final List<String> CANCEL_REQUEST_STATUSES = List.of(ReservationStatus.CANCEL_REQUEST);

    private Pageable ownerPageable(PageRequestDTO pageRequestDTO) {
        return PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("reservationId").descending());
    }

    /** 0 이하 ID 는 "전체" 를 뜻하므로 null 로 바꿔 쿼리의 조건을 끈다. */
    private static Integer scopeId(Integer id) {
        return (id != null && id > 0) ? id : null;
    }

    private static Long scopeId(Long id) {
        return (id != null && id > 0) ? id : null;
    }

    /**
     * 목록과 totalCount 가 같은 상태 조건을 쓰도록 필터링은 리포지토리 쿼리(페이지 + count)에서 한다.
     * (이전에는 페이지를 가져온 뒤 메모리에서 걸러 total 이 취소 건까지 세는 문제가 있었다.)
     */
    private PageResponseDTO<ReservationDTO> toPageResponse(Page<Reservation> page, PageRequestDTO pageRequestDTO) {
        List<ReservationDTO> list = page.getContent().stream()
                .map(this::toDTO)
                .toList();

        return PageResponseDTO.<ReservationDTO>withAll()
                .dtoList(list)
                .totalCount(page.getTotalElements())
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    public PageResponseDTO<ReservationDTO> getReservationsByCompanyIdWithPageable(Integer companyId, PageRequestDTO pageRequestDTO) {
        Page<Reservation> page = reservationRepository.findPageByCompanyExcludingStatuses(
                scopeId(companyId), INACTIVE_STATUSES, ownerPageable(pageRequestDTO));
        return toPageResponse(page, pageRequestDTO);
    }

    public PageResponseDTO<ReservationDTO> getCancelRequestReservationsByCompanyIdWithPageable(Integer companyId, PageRequestDTO pageRequestDTO) {
        Page<Reservation> page = reservationRepository.findPageByCompanyWithStatuses(
                scopeId(companyId), CANCEL_REQUEST_STATUSES, ownerPageable(pageRequestDTO));
        return toPageResponse(page, pageRequestDTO);
    }

    public PageResponseDTO<ReservationDTO> getReservationsByAccommodationIdWithPageable(Long accommodationId, PageRequestDTO pageRequestDTO) {
        Page<Reservation> page = reservationRepository.findPageByAccommodationExcludingStatuses(
                scopeId(accommodationId), INACTIVE_STATUSES, ownerPageable(pageRequestDTO));
        return toPageResponse(page, pageRequestDTO);
    }

    public PageResponseDTO<ReservationDTO> getCancelRequestReservationsByAccommodationIdWithPageable(Long accommodationId, PageRequestDTO pageRequestDTO) {
        Page<Reservation> page = reservationRepository.findPageByAccommodationWithStatuses(
                scopeId(accommodationId), CANCEL_REQUEST_STATUSES, ownerPageable(pageRequestDTO));
        return toPageResponse(page, pageRequestDTO);
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
