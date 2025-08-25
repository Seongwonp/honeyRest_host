package com.honeyrest.honeyrest_host.entity;


import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation")
public class Reservation extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    public Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id",  nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation; // 숙소 ID(중복 저장)

    @Column(name = "accommodation_name", nullable = false, length = 255)
    private String accommodationName; // 숙소명

    @Column(name = "room_name", nullable = false, length = 255)
    private String roomName; // 객실명

    @Column(name = "reservation_number",nullable = false, unique = true, length = 50 )
    private String reservationNumber; // 고유 예약 번호

    @Column(name = "check_in_date",nullable = false)
    private LocalDate checkInDate; // 체크인

    @Column(name = "check_out_date" ,nullable = false)
    private LocalDate checkOutDate; // 체크 아웃

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    @Column(name = "guest_name", nullable = false, length = 100)
    private String guestName;

    @Column(name = "guest_phone", nullable = false, length = 20)
    private String guestPhone;

    @Column(name = "price", nullable = false, length = 20)
    private BigDecimal price; // 최종 결제 금액

    @Column(name = "original_price",precision = 10, scale = 2)
    private BigDecimal originalPrice; // 할인 전 원가

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount; // 총 할인 금액

    @Column(name = "status", nullable = false, length = 20)
    private String status; // 예약 상태

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason; // 취소 사유

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequest; // 특별 요청 사항

    @Version
    private Long version; // ✅ 낙관적 락

    // -------- 생성 시 필수값 검증 (엔티티 내부) --------
    public void validateNew() {
        if (user == null)           throw new IllegalStateException("user는 필수입니다.");
        if (room == null)           throw new IllegalStateException("room은 필수입니다.");
        if (accommodation == null)    throw new IllegalStateException("accommodationId는 필수입니다.");
        if (accommodationName == null)  throw new IllegalStateException("accommodationName은 필수입니다.");
        if (roomName == null)           throw new IllegalStateException("roomName은 필수입니다.");
        if (reservationNumber == null)  throw new IllegalStateException("reservationNumber는 필수입니다.");
        if (checkInDate == null)        throw new IllegalStateException("checkInDate는 필수입니다.");
        if (checkOutDate == null)       throw new IllegalStateException("checkOutDate는 필수입니다.");
        if (guestCount == null)         throw new IllegalStateException("guestCount는 필수입니다.");
        if (guestName == null)          throw new IllegalStateException("guestName은 필수입니다.");
        if (guestPhone == null)         throw new IllegalStateException("guestPhone는 필수입니다.");
        if (price == null)              throw new IllegalStateException("price는 필수입니다.");
        if (status == null)             throw new IllegalStateException("status는 필수입니다.");
        validateDates();
    }

    private void validateDates() {
        if (checkInDate != null && checkOutDate != null && !checkInDate.isBefore(checkOutDate)) {
            throw new IllegalArgumentException("체크인 날짜는 체크아웃 이전이어야 합니다.");
        }
    }

    // -------- 부분 업데이트: 세터 없이 DTO로 업데이트 --------
    public void update(ReservationDTO dto, User newUser, Room newRoom, Accommodation newAccommodation) {
        if (dto.getCheckInDate() != null)  this.checkInDate = dto.getCheckInDate();
        if (dto.getCheckOutDate() != null) this.checkOutDate = dto.getCheckOutDate();
        validateDates();

        if (dto.getGuestCount() != null)   this.guestCount = dto.getGuestCount();
        if (dto.getGuestName() != null)    this.guestName = dto.getGuestName();
        if (dto.getGuestPhone() != null)   this.guestPhone = dto.getGuestPhone();
        if (dto.getPrice() != null)        this.price = dto.getPrice();
        if (dto.getStatus() != null)       this.status = dto.getStatus();
        if (dto.getCancelReason() != null) this.cancelReason = dto.getCancelReason();
        if (dto.getSpecialRequest() != null) this.specialRequest = dto.getSpecialRequest();

        if (dto.getAccommodationName() != null) this.accommodationName = dto.getAccommodationName();

        // 예약번호는 보통 불변(변경 비권장). 정말 필요하면 아래 주석 해제
        // if (dto.getReservationNumber() != null) this.reservationNumber = dto.getReservationNumber();

        if (newUser != null) this.user = newUser;
        if (newRoom != null) this.room = newRoom;
        if (newAccommodation != null) this.accommodation = newAccommodation;
        if (newRoom != null && dto.getRoomId() != null) {
            // roomName은 보통 Room 엔티티의 필드에서 가져오거나 DTO로 받음
            // this.roomName = newRoom.getName(); // 필요 시
        }
    }

    // 도메인 메서드
    public void cancel(String reason) {
        if ("CANCELLED".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }
        this.status ="CANCELLED";
        this.cancelReason = reason;
    }
}
