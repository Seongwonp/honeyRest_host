package com.honeyrest.honeyrest_host.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId; // 결제 고유 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", length = 30, nullable = false)
    private String paymentMethod;

    @Column(name = "payment_status", length = 20, nullable = false)
    private String paymentStatus;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "pg_provider", length = 50)
    private String pgProvider; // pg사 이름

    @Column(name = "receipt_url", length = 500)
    private String receiptUrl; // 영수증 url

    @Column(name = "payment_date")
    private LocalDateTime paymentDate; // 결제일시



}
