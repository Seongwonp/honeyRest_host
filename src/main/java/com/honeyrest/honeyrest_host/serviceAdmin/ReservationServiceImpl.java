package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoAdmin.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.PaymentDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.*;
import com.honeyrest.honeyrest_host.repositoryAdmin.PaymentRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationInventoryGuard;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AccommodationRepository accommodationRepository;
    private final ReservationInventoryGuard inventoryGuard;
    // 예약 점유가 바뀌면 사용자 API 검색 캐시 세대를 커밋 후 올린다 (검색 결과의 남은 객실 수가 바뀜)
    private final SearchCacheInvalidator searchCacheInvalidator;


    @Override
    public ReservationDTO getReservationByNumber(String number) {
        Reservation r = reservationRepository.findByReservationNumber(number)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. 예약번호=" + number));
        return modelMapper.map(r, ReservationDTO.class);
    }

    @Override
    public ReservationDTO getReservationById(Long reservationId) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약이없습니다"));
        return toDto(r);
    }

    // 수정: 세터 대신 도메인 메서드 사용
    @Override
    public ReservationDTO updateReservation(ReservationDTO dto) {

        // 기존 엔티티
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + dto.getReservationId()));

        // 연관 엔티티
        User newUser = null;
        Room newRoom = null;
        Accommodation newAccommodation = null;

        if (dto.getUserId() != null) {
            newUser = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다. userId=" + dto.getUserId()));
        }
        if (dto.getRoomId() != null) {
            newRoom = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new EntityNotFoundException("객실을 찾을 수 없습니다. roomId=" + dto.getRoomId()));
        }
        if (dto.getAccommodationId() != null) {
            newAccommodation = accommodationRepository.findById(dto.getAccommodationId())
                    .orElseThrow(() -> new EntityNotFoundException("숙소를 찾을 수 없습니다. id=" + dto.getAccommodationId()));
        }

        // (선택) 기본 검증 예시: 체크인/아웃 역전 방지
        if (dto.getCheckInDate() != null && dto.getCheckOutDate() != null
            && !dto.getCheckInDate().isBefore(dto.getCheckOutDate())) {
            throw new IllegalArgumentException("체크인 날짜는 체크아웃보다 이전이어야 합니다.");
        }

        // 상태 정규화 + 점유 상태로 들어가거나(예: CANCELLED → CONFIRMED) 점유 중 객실/기간이 바뀌면 재고 검사
        String oldStatus = reservation.getStatus();
        String newStatus = ReservationStatus.normalize(dto.getStatus(), oldStatus);
        dto.setStatus(newStatus);
        Long oldRoomId = reservation.getRoom() != null ? reservation.getRoom().getRoomId() : null;
        Long newRoomId = dto.getRoomId() != null ? dto.getRoomId() : oldRoomId;
        LocalDate newIn = dto.getCheckInDate() != null ? dto.getCheckInDate() : reservation.getCheckInDate();
        LocalDate newOut = dto.getCheckOutDate() != null ? dto.getCheckOutDate() : reservation.getCheckOutDate();
        boolean enteringOccupying = ReservationStatus.isOccupying(newStatus)
                && !ReservationStatus.isOccupying(oldStatus);
        boolean occupyingChanged = ReservationStatus.isOccupying(newStatus)
                && (!java.util.Objects.equals(oldRoomId, newRoomId)
                    || !java.util.Objects.equals(reservation.getCheckInDate(), newIn)
                    || !java.util.Objects.equals(reservation.getCheckOutDate(), newOut));
        if (enteringOccupying || occupyingChanged) {
            inventoryGuard.lockRoomAndAssertAvailable(newRoomId, newIn, newOut, reservation.getReservationId());
        }

        boolean leavingOccupying = ReservationStatus.isOccupying(oldStatus)
                && !ReservationStatus.isOccupying(newStatus);

        // 3) 도메인 메서드로 필드 반영 (JPA 변경감지)
        reservation.update(dto, newUser, newRoom, newAccommodation);

        if (enteringOccupying || leavingOccupying || occupyingChanged) {
            searchCacheInvalidator.bumpAfterCommit();
        }

        // 4) 트랜잭션 커밋 시 자동 flush. 여기서 DTO로 변환해 반환
        return toDto(reservation);
    }

    // 취소
    @Override
    public void cancelReservation(Long reservationId, String cancelReason) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));

        // 이미 취소면 무시
        if (ReservationStatus.CANCELLED.equalsIgnoreCase(r.getStatus())) return;

        // 상태 변경만 한다. 재고는 room.total_rooms − 겹치는 점유 예약 수로 계산하므로
        // 취소(CANCELLED, 비점유)되는 순간 자동으로 복구된다. total_rooms 를 늘리지 않는다.
        r.cancel(cancelReason);
        searchCacheInvalidator.bumpAfterCommit();
    }


    @Override
    public List<ReservationDTO> findRoomReservationsOverlapping(Long roomId, LocalDate start, LocalDate end) {
        // checkIn < end  AND  checkOut > start  (겹치는 예약 전부)
        List<Reservation> rows = reservationRepository
                .findByRoomIdAndDateBetween(roomId, start, end);

        return rows.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ReservationDTO> findCompanyReservationsOverlapping(Integer companyId, Long accommodationId, LocalDate start, LocalDate end) {

        // 회사 기준(숙소 선택 가능)으로 월 범위에 “겹치는” 예약들
        List<Reservation> rows = reservationRepository
                .findOverlappedReservationsForMonth(companyId, accommodationId, start, end);

        return rows.stream()
                .map(this::toDto)
                .toList();
    }


    @Override
    public ReservationDTO getReservationDetail(Long id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약 없음"));

        // 결제 최신건 조회
        Optional<Payment> paymentOpt = paymentRepository
                .findTopByReservationReservationIdOrderByCreatedAtDesc(id);

        PaymentDTO paymentDTO = paymentOpt.map(p -> PaymentDTO.builder()
                .paymentId(p.getPaymentId())
                .reservationNumber(r.getReservationNumber())
                .guestName(r.getGuestName())
                .guestPhone(r.getGuestPhone())
                .accommodationName(r.getAccommodation().getName())
                .roomName(r.getRoom() != null ? r.getRoom().getName() : null)
                .paymentMethod(p.getPaymentMethod())
                .paymentStatus(p.getPaymentStatus())
                .amount(p.getAmount())
                .paymentDate(p.getPaymentDate() != null ? p.getPaymentDate() : p.getCreatedAt())
                .build()
        ).orElse(null);

        return ReservationDTO.builder()
                .reservationId(r.getReservationId())
                .reservationNumber(r.getReservationNumber())

                .accommodationId(r.getAccommodation() != null ? r.getAccommodation().getAccommodationId() : null)
                .accommodationName(r.getAccommodation().getName())

                .roomId(r.getRoom() != null ? r.getRoom().getRoomId() : null)
                .roomName(r.getRoom() != null ? r.getRoom().getName() : null)

                .userId(r.getUser() != null ? r.getUser().getUserId() : null)

                .checkInDate(r.getCheckInDate())
                .checkOutDate(r.getCheckOutDate())

                .guestCount(r.getGuestCount())
                .guestName(r.getGuestName())
                .guestPhone(r.getGuestPhone())

                .price(r.getPrice())
                .originalPrice(r.getOriginalPrice())
                .discountAmount(r.getDiscountAmount())

                .status(r.getStatus())
                .cancelReason(r.getCancelReason())
                .specialRequest(r.getSpecialRequest())

                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())

                .payment(paymentDTO)
                .build();
    }

    // 간단한 예약번호 생성(원하면 바꿔도 됨)
    private String genReservationNumber() {
        // 예: HR-20240818-랜덤6자리
        String rand = Long.toString(System.nanoTime(), 36).toUpperCase();
        return "HR-" + java.time.LocalDate.now() + "-" + rand.substring(Math.max(0, rand.length() - 6));
    }

    @Override
    public List<ReservationDTO> findCompanyReservationsOnDate(Integer companyId,
                                                              Long accommodationId,
                                                              LocalDate date) {
        LocalDate start = date;
        LocalDate end = date.plusDays(1);

        List<Reservation> rows = reservationRepository
                .findOverlappedReservationsForMonth(companyId, accommodationId, start, end);

        return rows.stream().map(this::toDto).toList();
    }

    @Override
    public PageResponseDTO<ReservationDTO> getCancelRequestsForCompany(Integer companyId, String q, PageRequestDTO pr) {
        Pageable pageable = pr.getPageable(Sort.by("reservationId").descending());

        String normQ = (q == null || q.isBlank()) ? null : q.trim();
        // 상태는 고정
        String status = ReservationStatus.CANCEL_REQUEST;

        Page<Reservation> page = reservationRepository
                .searchCompanyReservations(companyId, status, normQ, null, pageable);

        List<ReservationDTO> list = page.getContent().stream()
                .map(this::toDto)
                .toList();

        return PageResponseDTO.<ReservationDTO>withAll()
                .pageRequestDTO(pr)
                .dtoList(list)
                .total((int) page.getTotalElements())
                .build();
    }


    @Transactional
    @Override
    public ReservationDTO createReservation(ReservationDTO dto) {
        dto.setReservationId(null);

        if (dto.getRoomId() == null) throw new IllegalStateException("roomId는 필수입니다.");
        if (dto.getUserId() == null) throw new IllegalStateException("userId는 필수입니다.");
        if (dto.getCheckInDate() != null && dto.getCheckOutDate() != null
            && !dto.getCheckInDate().isBefore(dto.getCheckOutDate())) {
            throw new IllegalArgumentException("체크인 날짜는 체크아웃보다 이전이어야 합니다.");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다. userId=" + dto.getUserId()));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("객실을 찾을 수 없습니다. roomId=" + dto.getRoomId()));
        Accommodation accommodation = accommodationRepository.findById(
                dto.getAccommodationId() != null ? dto.getAccommodationId() : room.getAccommodation().getAccommodationId()
        ).orElseThrow(() -> new EntityNotFoundException("숙소를 찾을 수 없습니다."));

        // 관리자 직접 생성의 기본 상태는 CONFIRMED. 점유 상태면 객실 행을 잠그고 겹침 검사를 한다.
        // (과거에는 room.total_rooms 를 1 줄였으나, 사용자 저장소와 같은 겹침 계산으로 통일했다.)
        String status = ReservationStatus.normalize(dto.getStatus(), ReservationStatus.CONFIRMED);
        if (ReservationStatus.isOccupying(status)) {
            inventoryGuard.lockRoomAndAssertAvailable(room.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate(), null);
        }

        String reservationNumber =
                (dto.getReservationNumber() != null && !dto.getReservationNumber().isBlank())
                        ? dto.getReservationNumber()
                        : genReservationNumber();

        Reservation entity = Reservation.builder()
                .user(user)
                .room(room)
                .accommodation(accommodation)
                .accommodationName(accommodation.getName())
                .roomName(room.getName())
                .reservationNumber(reservationNumber)
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .guestCount(dto.getGuestCount())
                .guestName(dto.getGuestName())
                .guestPhone(dto.getGuestPhone())
                .price(dto.getPrice())
                .originalPrice(dto.getOriginalPrice() != null ? dto.getOriginalPrice() : dto.getPrice())
                .discountAmount(dto.getDiscountAmount())
                .status(status)
                .cancelReason(dto.getCancelReason())
                .specialRequest(dto.getSpecialRequest())
                .build();

        entity.validateNew(); // 있으면 유지

        Reservation saved = reservationRepository.save(entity);
        if (ReservationStatus.isOccupying(status)) {
            searchCacheInvalidator.bumpAfterCommit();
        }
        return toDto(saved);
    }


    private ReservationDTO toDto(Reservation r) {
        Accommodation acc = r.getAccommodation();
        Room room = r.getRoom();
        ReservationDTO dto = ReservationDTO.builder()
                .accommodationId(acc != null ? acc.getAccommodationId() : null)
                .accommodationName(acc != null ? acc.getName() : r.getAccommodationName())
                .reservationNumber(r.getReservationNumber())
                .reservationId(r.getReservationId())
                .roomId(room != null ? room.getRoomId() : null)
                .roomName(room != null ? room.getName() : r.getRoomName())
                .discountAmount(r.getDiscountAmount())
                .price(r.getPrice())
                .originalPrice(r.getOriginalPrice())
                .cancelReason(r.getCancelReason())
                .checkInDate(r.getCheckInDate())
                .checkOutDate(r.getCheckOutDate())
                .guestName(r.getGuestName())
                .guestPhone(r.getGuestPhone())
                .guestCount(r.getGuestCount())
                .specialRequest(r.getSpecialRequest())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();

        // User 정보도 같이 매핑
        if (r.getUser() != null) {
            dto.setUserId(r.getUser().getUserId());
            dto.setUserName(r.getUser().getName());
        }

        // guestName이 없으면 userName으로 대체
        if (dto.getGuestName() == null || dto.getGuestName().isBlank()) {
            dto.setGuestName(dto.getUserName());
        }

        return dto;

    }

    @Override
    public PageResponseDTO<ReservationDTO> getCompanyReservations(Integer companyId,
                                                                  String status,
                                                                  String q,
                                                                  PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable(Sort.by("reservationId").descending());

        String st = null;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            st = status.toUpperCase();
            // CANCELLED 도 허용해야 결제 화면의 미결제(취소) 목록 조회가 동작한다.
            if (!ReservationStatus.ALL.contains(st)) {
                throw new IllegalArgumentException("유효하지 않은 예약 상태: " + status);
            }
        }

        Page<Reservation> page = reservationRepository.findCompanyReservations(companyId, st, q, pageable);

        List<ReservationDTO> dtoList = page.getContent().stream()
                .map(this::toDto)
                .toList();

        return PageResponseDTO.<ReservationDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) page.getTotalElements())
                .build();

    }

    @Override
    public Page<ReservationDTO> getCompanyReservations(
            Integer companyId, String status, String q, Long accId, Pageable pageable) {

        String statusParam = normalizeStatus(status); // ALL/빈값 → null
        String qParam = normalizeBlankToNull(q);

        Page<Reservation> page = reservationRepository.searchCompanyReservations(
                companyId, statusParam, qParam, accId, pageable);

        // Page<Reservation> → Page<ReservationDTO>
        return page.map(this::toDto);
    }

    /* 예약 상태에 따른 로직(총 5가지) */
    @Override
    public ReservationDTO approveCancelRequest(Long reservationId, String reason) {
        int updated = reservationRepository.approveCancelRequest(
                reservationId, reason, LocalDateTime.now());

        if (updated == 0) {
            throw new IllegalStateException("취소요청 상태의 예약만 승인할 수 있습니다. id=" + reservationId);
        }
        // CANCEL_REQUEST(점유) → CANCELLED(비점유): 객실이 다시 팔 수 있게 되므로 검색 캐시를 버린다.
        searchCacheInvalidator.bumpAfterCommit();
        log.info("예약 취소 승인 완료: reservationId={}, reason={}", reservationId, reason);
        return null;
    }

    /*
     * 아래 거부(CANCEL_REQUEST→CONFIRMED)·체크아웃 완료(CONFIRMED→COMPLETED)·노쇼(→NO_SHOW)는
     * 점유 → 점유 전이라 남은 객실 수가 바뀌지 않으므로 검색 캐시 세대를 올리지 않는다.
     * (ReservationStatus.OCCUPYING 참고)
     */
    @Override
    public void rejectCancelRequest(Long reservationId, String reason) {
        int updated = reservationRepository.rejectCancelRequest(
                reservationId, reason, LocalDateTime.now());

        if (updated == 0) {
            throw new IllegalStateException("취소요청 상태의 예약만 거부할 수 있습니다. id=" + reservationId);
        }
        log.info("예약 취소 거부 완료: reservationId={}, reason={}", reservationId, reason);
    }

    @Override
    public void markCompleted(Long reservationId) {
        int updated = reservationRepository.markCompleted(reservationId, LocalDateTime.now());

        if (updated == 0) {
            throw new IllegalStateException("CONFIRMED 상태의 예약만 COMPLETED로 변경 가능합니다. id=" + reservationId);
        }
        log.info("체크아웃 완료 처리: reservationId={}", reservationId);
    }

    @Override
    public void markNoShow(Long reservationId) {
        int updated = reservationRepository.markNoShow(reservationId, LocalDateTime.now());

        if (updated == 0) {
            throw new IllegalStateException("PENDING/CONFIRMED 상태만 NO_SHOW 처리 가능합니다. id=" + reservationId);
        }
        log.info("노쇼 처리 완료: reservationId={}", reservationId);
    }

    // ===================================================

    private String normalizeStatus(String status) {
        if (status == null) return null;
        String s = status.trim();
        return (s.isEmpty() || "ALL".equalsIgnoreCase(s)) ? null : s;
    }

    private String normalizeBlankToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}
