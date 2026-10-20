package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoAdmin.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.PaymentDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.ReservationDTO;
import com.honeyrest.domain.entity.*;
import com.honeyrest.domain.type.*;
import com.honeyrest.honeyrest_host.repositoryAdmin.PaymentRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationInventoryGuard;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationModificationService;
import com.honeyrest.honeyrest_host.serviceCommon.refund.ReservationRefundService;
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
    // 예약 수정 공통 규칙 (오너 화면과 공유)
    private final ReservationModificationService modificationService;
    // 취소/취소 승인 시 결제 취소·포인트 환불·쿠폰 복원
    private final ReservationRefundService refundService;
    // 예약 점유가 바뀌면 사용자 API 검색 캐시를 커밋 후 무효화 (남은 객실 수 변경)
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

    /**
     * 예약 수정 (부분 업데이트). 상태는 도메인 changeStatus 로만 바꾼다.
     * 락 순서: 예약 행 잠금 → (필요 시) 객실 락 + 재고 검사 → 연관 엔티티 조회.
     */
    @Override
    public ReservationDTO updateReservation(ReservationDTO dto) {
        Reservation reservation = reservationRepository.findByIdForUpdate(dto.getReservationId())
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + dto.getReservationId()));

        ReservationModificationService.Plan plan = modificationService.prepare(
                reservation, dto.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate(), dto.getStatus());

        // 연관 엔티티 (재고 검사 뒤에 읽는다)
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

        modificationService.apply(reservation, plan, dto, newUser, newRoom, newAccommodation);
        // 트랜잭션 커밋 시 변경 감지로 반영된다
        return toDto(reservation);
    }

    /**
     * 관리자 취소 + 환불(결제 취소·포인트·쿠폰 복원). 이미 취소된 예약이면 아무것도 하지 않는다(멱등).
     * 재고는 겹치는 점유 예약 수로 계산하므로 CANCELLED 가 되면 자동 복구된다.
     */
    @Override
    public void cancelReservation(Long reservationId, String cancelReason) {
        Reservation r = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));

        if (ReservationStatus.CANCELLED.equalsIgnoreCase(r.getStatus())) return;

        r.cancel(cancelReason);
        refundService.refund(r, cancelReason);
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

        // 기본 상태는 CONFIRMED. 객실 락이 트랜잭션의 첫 쿼리여야 한다
        // (일반 SELECT 를 먼저 하면 REPEATABLE READ 스냅샷이 고정돼 겹침 수가 옛 값을 본다).
        String status = ReservationStatus.normalize(dto.getStatus(), ReservationStatus.CONFIRMED);
        Room room;
        if (ReservationStatus.isOccupying(status)) {
            room = inventoryGuard.lockRoomAndAssertAvailable(dto.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate(), null);
        } else {
            room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new EntityNotFoundException("객실을 찾을 수 없습니다. roomId=" + dto.getRoomId()));
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다. userId=" + dto.getUserId()));
        Accommodation accommodation = accommodationRepository.findById(
                dto.getAccommodationId() != null ? dto.getAccommodationId() : room.getAccommodation().getAccommodationId()
        ).orElseThrow(() -> new EntityNotFoundException("숙소를 찾을 수 없습니다."));

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
            // 결제 화면의 미결제(취소) 목록 조회를 위해 CANCELLED 도 허용
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

    /*
     * 예약 상태 전이. 예약 행을 잠금 읽기로 가져와 도메인 메서드로만 바꾼다(잘못된 전이는 IllegalStateException).
     */

    /** 취소 요청 승인: CANCEL_REQUEST → CANCELLED + 환불. 갱신된 예약을 돌려준다. */
    @Override
    public ReservationDTO approveCancelRequest(Long reservationId, String reason) {
        Reservation r = lockedReservation(reservationId);
        r.approveCancelRequest(reason);
        refundService.refund(r, r.getCancelReason());
        // 점유 → 비점유: 객실을 다시 팔 수 있으므로 검색 캐시 무효화
        searchCacheInvalidator.bumpAfterCommit();
        log.info("예약 취소 승인 완료: reservationId={}, reason={}", reservationId, reason);
        return toDto(r);
    }

    // 아래 전이는 점유 → 점유라 남은 객실 수가 같으므로 검색 캐시를 건드리지 않는다
    @Override
    public void rejectCancelRequest(Long reservationId, String reason) {
        lockedReservation(reservationId).rejectCancelRequest(reason);
        log.info("예약 취소 거부 완료: reservationId={}, reason={}", reservationId, reason);
    }

    @Override
    public void markCompleted(Long reservationId) {
        lockedReservation(reservationId).complete();
        log.info("체크아웃 완료 처리: reservationId={}", reservationId);
    }

    @Override
    public void markNoShow(Long reservationId) {
        lockedReservation(reservationId).markNoShow();
        log.info("노쇼 처리 완료: reservationId={}", reservationId);
    }

    private Reservation lockedReservation(Long reservationId) {
        return reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));
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
