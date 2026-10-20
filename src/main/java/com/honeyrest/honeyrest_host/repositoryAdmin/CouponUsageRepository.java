package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.domain.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 쿠폰 사용 내역(coupon_usage)과 회원 쿠폰 상태 복원. 예약 취소 환불(ReservationRefundService) 전용.
 */
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    @Query("select cu from CouponUsage cu join fetch cu.userCoupon where cu.reservation.reservationId = :reservationId")
    List<CouponUsage> findByReservationId(@Param("reservationId") Long reservationId);

    /**
     * 사용된 회원 쿠폰을 ISSUED 로 되돌린다. 이미 복원됐으면 0 (멱등).
     * 영속성 컨텍스트를 비우면 호출자의 예약 엔티티 변경이 유실되므로 clearAutomatically 는 쓰지 않는다.
     */
    @Modifying(flushAutomatically = true)
    @Query("""
            update UserCoupon uc
               set uc.status = :issued, uc.usedAt = null
             where uc.userCouponId = :userCouponId
               and uc.status = :used
            """)
    int restoreUserCoupon(@Param("userCouponId") Long userCouponId,
                          @Param("used") String used,
                          @Param("issued") String issued);
}
