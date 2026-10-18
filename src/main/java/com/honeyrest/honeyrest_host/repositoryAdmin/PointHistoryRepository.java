package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.domain.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 포인트 이력(point_history). 사용자 API 가 예약 시 사용한 포인트를 type=USE, related_id=예약 ID, amount=음수로 남긴다.
 * 호스트는 예약 취소 환불(ReservationRefundService)에서만 읽고 쓴다.
 */
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    /** 회원·관련 예약·유형별 변동량 합계 (USE 는 음수 합, REFUND 는 양수 합). 이력이 없으면 0. */
    @Query("""
            select coalesce(sum(ph.amount), 0) from PointHistory ph
             where ph.user.userId = :userId
               and ph.relatedId = :relatedId
               and ph.type = :type
            """)
    long sumAmount(@Param("userId") Long userId,
                   @Param("relatedId") Long relatedId,
                   @Param("type") String type);
}
