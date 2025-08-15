package com.honeyrest.honeyrest_host.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    private Long reservationId; // 예약 고유 아이디(식별자)

    private String reservationNumber; // 예약 번호

    @NotNull
    private Long accommodationId;

    @NotNull
    private String accommodationName; // 숙소명

    @NotNull
    private Long userId;

    private Long roomId;

    private LocalDate checkInDate; // 날짜 체크인
    private LocalDate checkOutDate; // 날짜 체크아웃

    @NotNull
    private Integer guestCount; // 인원

    @NotNull
    private String guestName; // 예약자 이름

    @NotNull
    private String guestPhone; // 예약자 연락처

    @NotNull
    private BigDecimal price; // 예약 금액

    private String status; // 예약 상태(예약, 취소, 체크인, 체크아웃 등)

    private String cancelReason; // 취소 사유

    private String specialRequest; // 특별 요청 사항


    private LocalDateTime createdAt; // 생성일
    private LocalDateTime updatedAt; // 수정일


}

