package com.honeyrest.honeyrest_host.dto;


import com.honeyrest.honeyrest_host.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private String reservationNumber;
    private String guestName;
    private String guestPhone;
    private String accommodationName;
    private String roomName;
    private String paymentMethod; // card, cash,
    private String paymentStatus;
    private BigDecimal amount;         // 환불은 음수
    private LocalDateTime paymentDate; // 결제/환불 날짜
    private String pgProvider;

    private LocalDateTime createdAt;


    // 엔티티 -> dto 반환
    public static PaymentDTO of(Payment p) {
        return PaymentDTO.builder()
                .paymentId(p.getPaymentId())
                .reservationNumber(p.getReservation().getReservationNumber())
                .guestName(p.getReservation().getGuestName())
                .guestPhone(p.getReservation().getGuestPhone())
                .accommodationName(p.getReservation().getAccommodationName())
                .roomName(p.getReservation().getRoomName())
                .paymentMethod(p.getPaymentMethod())
                .paymentStatus(p.getPaymentStatus())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .paymentStatus(p.getPaymentStatus())
                .pgProvider(p.getPgProvider())
                .paymentDate(p.getPaymentDate())
                .createdAt(p.getCreatedAt())
                .build();
    }
//        Reservation r = p.getReservation();
//        BigDecimal amt = p.getAmount();
//        if ("REFUNDED".equalsIgnoreCase(p.getPaymentStatus())) {
//            amt = amt.negate(); // 화면에서는 환불을 음수로 표시
}
