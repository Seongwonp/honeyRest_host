package com.honeyrest.honeyrest_host.dto;


import com.honeyrest.honeyrest_host.entity.Payment;
import com.honeyrest.honeyrest_host.entity.Reservation;
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


    // 엔티티 -> dto 반환
    public static PaymentDTO of(Payment p) {
        Reservation r = p.getReservation();
        BigDecimal amt = p.getAmount();
        if ("REFUNDED".equalsIgnoreCase(p.getPaymentStatus())) {
            amt = amt.negate(); // 화면에서는 환불을 음수로 표시
        }
        return PaymentDTO.builder()
                .paymentId(p.getPaymentId())
                .reservationNumber(r.getReservationNumber())
                .guestName(r.getGuestName())
                .guestPhone(r.getGuestPhone())
                .accommodationName(r.getAccommodationName())
                .roomName(r.getRoomName())
                .paymentMethod(p.getPaymentMethod())
                .paymentStatus(p.getPaymentStatus())
                .amount(amt)
                .paymentDate(p.getPaymentDate())
                .build();
    }
}
