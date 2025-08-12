package com.honeyrest.honeyrest_host.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon_usage")
public class CouponUsage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long usageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id", nullable = false)
    private UserCoupon userCoupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reseration_id", nullable = false)
    private Reservation reservation;


    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount; // 할인 적용 금액



}
