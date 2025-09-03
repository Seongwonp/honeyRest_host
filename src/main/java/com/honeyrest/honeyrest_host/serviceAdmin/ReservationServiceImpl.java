package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.PaymentDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.*;
import com.honeyrest.honeyrest_host.repositoryAdmin.PaymentRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
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

        // 3) 도메인 메서드로 필드 반영 (JPA 변경감지)
        reservation.update(dto, newUser, newRoom, newAccommodation);

        // 4) 트랜잭션 커밋 시 자동 flush. 여기서 DTO로 변환해 반환
        return toDto(reservation);
    }

    // 취소
    @Override
    public void cancelReservation(Long reservationId, String cancelReason) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));

        // 이미 취소면 중복 복구 방지
        if ("CANCELLED".equalsIgnoreCase(r.getStatus())) return;

        // 2) 재고 복구가 필요한 상태인지 판단
        boolean needRestock =
                ("CONFIRMED".equalsIgnoreCase(r.getStatus()));
        // 만약 완료(COMPLETED)에서도 재고를 차감했다면 ↓ 이렇게 확장
        // boolean needRestock = "CONFIRMED".equalsIgnoreCase(r.getStatus())
        //  || "COMPLETED".equalsIgnoreCase(r.getStatus());


        // 3) 상태 변경(도메인 메서드 권장: 내부에서 status, cancelReason, updatedAt 등 세팅)
//        r.cancel("CANCELLED");
        r.cancel(cancelReason);

        // 4) 재고 복구
        if (needRestock) {
            roomRepository.increaseStock(r.getRoom().getRoomId());
        }
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
    public List<ReservationDTO> findCompanyReservationsOverlapping(Long companyId, Long accommodationId, LocalDate start, LocalDate end) {

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
    public List<ReservationDTO> findCompanyReservationsOnDate(Long companyId,
                                                              Long accommodationId,
                                                              LocalDate date) {
        LocalDate start = date;
        LocalDate end = date.plusDays(1);

        List<Reservation> rows = reservationRepository
                .findOverlappedReservationsForMonth(companyId, accommodationId, start, end);

        return rows.stream().map(this::toDto).toList();
    }

    @Override
    public PageResponseDTO<ReservationDTO> getCancelRequestsForCompany(Long companyId, String q, PageRequestDTO pr) {
        Pageable pageable = pr.getPageable(Sort.by("reservationId").descending());

        String normQ = (q == null || q.isBlank()) ? null : q.trim();
        // 상태는 고정
        String status = "CANCEL_REQUEST";

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

        int updated = roomRepository.decreaseStock(room.getRoomId());
        if (updated == 0) throw new IllegalStateException("해당 객실 타입의 재고가 부족합니다. (품절)");

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
                .status(dto.getStatus() != null ? dto.getStatus() : "CONFIRMED")
                .cancelReason(dto.getCancelReason())
                .specialRequest(dto.getSpecialRequest())
                .build();

        entity.validateNew(); // 있으면 유지

        Reservation saved = reservationRepository.save(entity);
        return toDto(saved);
    }


    private ReservationDTO toDto(Reservation r) {
        ReservationDTO dto = ReservationDTO.builder()
                .accommodationId(r.getRoom().getAccommodation().getAccommodationId())
                .accommodationName(r.getRoom().getAccommodation().getName())
                .reservationNumber(r.getReservationNumber())
                .reservationId(r.getReservationId())
                .roomName(r.getRoom().getName())
                .discountAmount(r.getDiscountAmount())
                .price(r.getPrice())
                .originalPrice(r.getOriginalPrice())
                .cancelReason(r.getCancelReason())
                .checkInDate(r.getCheckInDate())
                .checkOutDate(r.getCheckOutDate())
                .guestName(r.getGuestName())
                .guestPhone(r.getGuestPhone())
                .guestCount(r.getGuestCount())
                .price(r.getPrice())
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
    public PageResponseDTO<ReservationDTO> getCompanyReservations(Long companyId,
                                                                  String status,
                                                                  String q,
                                                                  PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable(Sort.by("reservationId").descending());

        String st = null;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            st = status.toUpperCase();
            List<String> validStatuses = List.of("CONFIRMED", "PENDING", "COMPLETED", "CANCEL_REQUEST", "NO_SHOW");
            if (!validStatuses.contains(st)) {
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
            Long companyId, String status, String q, Long accId, Pageable pageable) {

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
        log.info("예약 취소 승인 완료: reservationId={}, reason={}", reservationId, reason);
        return null;
    }

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
