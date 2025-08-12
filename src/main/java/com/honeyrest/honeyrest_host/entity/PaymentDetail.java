package com.honeyrest.honeyrest_host.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_detail")
public class PaymentDetail extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_detail_id")
    private Long paymentDetailId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment; // 결제 FK

    @Column(name = "card_company", length = 50)
    private String cardCompany;

    @Column(name = "card_number", length = 50)
    private String cardNumber; // 마스킹된 카드번호

    @Column(name = "installment_months")
    private Integer installmentMonths; // 할부 개월

    @Column(name = "virtual_account_number", length = 50)
    private String virtualAccountNumber; // 가상계좌번호

    @Column(name = "virtual_account_bank", length = 50)
    private String virtualAccountBank; // 가상계좌 은행

    @Column(name = "virtual_account_holder", length = 50)
    private String virtualAccountHolder; // 예금주

    @Column(name = "virtual_account_expire")
    private LocalDateTime virtualAccountExpire; // 입금기한

    @Column(name = "toss_payment_key", length = 100)
    private String tossPaymentKey; // 토스 결제 키









}
