package com.honeyrest.honeyrest_host.dtoOwner;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponDTO {

    private Long couponId;
    private String name; // 쿠폰명
    private String code; // 고유 쿠폰 코드(자동 발급의 경우 null)
    private String discountType; // 할인 유형
    private BigDecimal discountValue; // 할인 값
    private BigDecimal minOrderAmount; // 최소 주문 금액
    private BigDecimal maxDiscountAmount; // 최대 주문 금액
    private String targetType; // 적용대상
    private Long targetId; // 특정 숙소, 카테고리 ID(null 가능)
    private boolean isActive; // 활성 여부
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
