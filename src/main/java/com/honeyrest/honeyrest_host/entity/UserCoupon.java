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
@Table(name = "user_coupon")
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(nullable = false,length = 20)
    private String status; // 상태 (ISSUED, USED, EXPIRED)

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt; // 발급 일시

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt; // 사용 일시

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt; // 만료 일시
}
