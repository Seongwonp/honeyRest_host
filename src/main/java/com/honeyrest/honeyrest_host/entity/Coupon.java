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
@Table(name = "coupon")
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "name", nullable = false, length = 50)
    private String name; // 쿠폰명

    @Column(name = "code", length = 50, unique = true)
    private String code; // 고유 쿠폰 코드(자동 발급의 경우 null)

    @Column(name = "discount_type", nullable = false)
    private String discountType; // 할인 유형

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue; // 할인 값

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount; // 최소 주문 금액

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxOrderAmount; // 최대 주문 금액

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType; // 적용대상

    @Column(name = "target_id")
    private Long targetId; // 특정 숙소, 카테고리 ID(null 가능)

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "is_active",nullable = false)
    private boolean isActive; // 활성 여부
}
