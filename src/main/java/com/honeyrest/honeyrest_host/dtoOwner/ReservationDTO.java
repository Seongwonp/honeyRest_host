package com.honeyrest.honeyrest_host.dtoOwner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDTO {
    private Long reservationId;
    private Long userId;
    private Long roomId;
    private Long accommodationId; // 숙소ID(중복 저장)
    private String accommodationName; // 숙소명
    private String roomName; // 객실명
    private String reservationNumber; // 고유 예약 번호
    private LocalDate checkInDate; // 체크인
    private LocalDate checkOutDate; // 체크 아웃
    private Integer guestCount;
    private String guestName;
    private String guestPhone;
    private BigDecimal price; // 최종 결제 금액
    private BigDecimal originalPrice; // 할인 전 원가
    private BigDecimal discountAmount; // 총 할인 금액
    private String status; // 예약 상태
    private String cancelReason; // 취소 사유
    private String specialRequest; // 특별 요청 사항
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
