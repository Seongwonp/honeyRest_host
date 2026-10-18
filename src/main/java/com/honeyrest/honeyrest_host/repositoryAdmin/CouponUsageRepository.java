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
     * 사용된(USED) 회원 쿠폰을 다시 발급 상태로 되돌린다. 공유 엔티티 UserCoupon 에 복원 메서드가 없어 JPQL 로 바꾼다.
     * 조건부 UPDATE 라 이미 복원된 쿠폰이면 0 을 돌려준다(멱등).
     * flushAutomatically: 같은 트랜잭션의 예약 상태 변경을 먼저 반영한다. 영속성 컨텍스트는 비우지 않는다
     * (비우면 호출자가 들고 있는 예약 엔티티 변경이 유실된다).
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
