package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.PaymentDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.*;
import com.honeyrest.honeyrest_host.repository.PaymentRepository;
import com.honeyrest.honeyrest_host.repository.ReservationRepository;
import com.honeyrest.honeyrest_host.repository.RoomRepository;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
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
import java.time.LocalDate;
import java.util.ArrayList;
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
        if ("CANCEL_REQUEST".equals(r.getStatus())) return;

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

    // 생성
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
        Accommodation accommodation = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new EntityNotFoundException("숙소를 찾을 수 없습니다. accommodationId=" + dto.getAccommodationId()));

        //  여기에서 인원 검증
        if (dto.getGuestCount() != null) {
            if (room.getStandardOccupancy() != null && dto.getGuestCount() < room.getStandardOccupancy()) {
                throw new IllegalArgumentException("기준 인원보다 적게 예약할 수 없습니다.");
            }
            if (room.getMaxOccupancy() != null && dto.getGuestCount() > room.getMaxOccupancy()) {
                throw new IllegalArgumentException("최대 인원을 초과했습니다.");
            }
        }
        //  1) 재고 차감 (원자적)
        int updated = roomRepository.decreaseStock(room.getRoomId());
        if (updated == 0) {
            throw new IllegalStateException("해당 객실 타입의 재고가 부족합니다. (품절)");
        }

        // 필요시 숙소 정보 자동 채우기
        Long accId = dto.getAccommodationId() != null ? dto.getAccommodationId() : room.getAccommodation().getAccommodationId();
        String accNm = dto.getAccommodationName() != null ? dto.getAccommodationName() : room.getAccommodation().getName();
        String roomName = room.getName();

        String reservationNumber = (dto.getReservationNumber() != null && !dto.getReservationNumber().isBlank())
                ? dto.getReservationNumber()
                : genReservationNumber();

        Reservation newEntity = Reservation.builder()
                .user(user)
                .room(room)
                .accommodation(accommodation)
                .accommodationName(accNm)
                .roomName(roomName)
                .reservationNumber(reservationNumber)
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .guestCount(dto.getGuestCount())
                .guestName(dto.getGuestName())
                .guestPhone(dto.getGuestPhone())
                .price(dto.getPrice())
                .originalPrice(dto.getPrice())
                .discountAmount(dto.getPrice() != null ? BigDecimal.ZERO : null)
                .status(dto.getStatus() != null ? dto.getStatus() : "CONFIRMED") // 생성과 동시에 확정
                .cancelReason(dto.getCancelReason())
                .specialRequest(dto.getSpecialRequest())
                .build();

        newEntity.validateNew();

        Reservation saved = reservationRepository.save(newEntity);
        return modelMapper.map(saved, ReservationDTO.class);
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
    public List<ReservationDTO> findCompanyReservationsOnDate(Long companyId, Long accommodationId, LocalDate date) {
        // “하루”를 보기 위해 start=date, end=date+1 로 겹침 검색
        LocalDate start = date;
        LocalDate end = date.plusDays(1);

        List<Reservation> rows = reservationRepository
                .findOverlappedReservationsForMonth(companyId, accommodationId, start, end);

        List<ReservationDTO> dtoList = new ArrayList<>();
        for (Reservation row : rows) {
            dtoList.add(toDto(row));
        }
        return dtoList;
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
                .paymentDate(p.getCreatedAt())
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


    private ReservationDTO toDto(Reservation r) {
        ReservationDTO dto = ReservationDTO.builder()
                .accommodationId(r.getRoom().getAccommodation().getAccommodationId())
                .accommodationName(r.getRoom().getAccommodation().getName())
                .reservationNumber(r.getReservationNumber())
                .reservationId(r.getReservationId())
                .roomName(r.getRoom().getName())
                .discountAmount(r.getDiscountAmount())
                .price(r.getPrice())
                .originalPrice(r.getPrice())
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

    private Reservation toEntity(ReservationDTO d) {
        return Reservation.builder()
                .accommodation(accommodationRepository.getReferenceById(d.getAccommodationId()))
                .user(userRepository.getReferenceById(d.getUserId()))
                .room(roomRepository.getReferenceById(d.getRoomId()))
                .price(d.getPrice())
                .originalPrice(d.getOriginalPrice())
                .discountAmount(d.getDiscountAmount())
                .accommodationName(d.getAccommodationName())
                .roomName(d.getRoomName())
                .cancelReason(d.getCancelReason())
                .checkInDate(d.getCheckInDate())
                .checkOutDate(d.getCheckOutDate())
                .guestName(d.getGuestName())
                .guestPhone(d.getGuestPhone())
                .guestCount(d.getGuestCount())
                .price(d.getPrice())
                .specialRequest(d.getSpecialRequest())
                .status(d.getStatus())
                .build();
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
            List<String> validStatuses = List.of("CONFIRMED", "PENDING", "COMPLETED", "CANCELLED", "NO_SHOW");
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
