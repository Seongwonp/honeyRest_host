package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public interface DashboardReportRepository  extends JpaRepository<Reservation, Long> {
    /** 판매(투숙) 객실박수: 기간과 겹치는 CONFIRMED/COMPLETED 예약의 박수 합 */
    @Query(value = """
        SELECT COALESCE(SUM(DATEDIFF(LEAST(r.check_out_date, DATE_ADD(:to, INTERVAL 1 DAY)),
                                     GREATEST(r.check_in_date, :from))), 0)
        FROM reservation r
        WHERE r.accommodation_id IN (:accIds)
          AND r.status IN ('CONFIRMED','COMPLETED')
          AND r.check_in_date < DATE_ADD(:to, INTERVAL 1 DAY)
          AND r.check_out_date > :from
        """, nativeQuery = true)
    Integer sumSoldNights(@Param("accIds") List<Long> accommodationIds,
                          @Param("from") LocalDate from,
                          @Param("to") LocalDate to);

    /** 기간 매출(결제일 기준, DONE) */
    @Query(value = """
        SELECT COALESCE(SUM(p.amount),0)
        FROM payment p
        JOIN reservation r ON r.reservation_id = p.reservation_id
        WHERE r.accommodation_id IN (:accIds)
          AND p.payment_status = 'DONE'
          AND p.payment_date >= :from
          AND p.payment_date < DATE_ADD(:to, INTERVAL 1 DAY)
        """, nativeQuery = true)
    BigDecimal sumRevenue(@Param("accIds") List<Long> accommodationIds,
                          @Param("from") LocalDate from,
                          @Param("to") LocalDate to);

    /** 가용 객실 수(운영 중인 객실 수) */
    @Query(value = """
        SELECT COUNT(*)
        FROM room rm
        WHERE rm.accommodation_id IN (:accIds)
          AND (rm.status = 1 OR rm.status = 'ACTIVE')
        """, nativeQuery = true)
    Integer countActiveRooms(@Param("accIds") List<Long> accommodationIds);

    /** 취소 요약: 기간 내 예약(체크인 기준) 전체/취소 수 */
    // before: Object[] cancelSummary(...)
    @Query(value = """
    SELECT
      (SELECT COUNT(*) FROM reservation r
        WHERE r.accommodation_id IN (:accIds)
          AND r.check_in_date >= :from AND r.check_in_date < DATE_ADD(:to, INTERVAL 1 DAY)
      ) AS totalCnt,
      (SELECT COUNT(*) FROM reservation r
        WHERE r.accommodation_id IN (:accIds)
          AND r.check_in_date >= :from AND r.check_in_date < DATE_ADD(:to, INTERVAL 1 DAY)
          AND r.status = 'CANCELED'
      ) AS canceledCnt
    """, nativeQuery = true)
    List<Object[]> cancelSummary(@Param("accIds") List<Long> accommodationIds,
                                 @Param("from") LocalDate from,
                                 @Param("to") LocalDate to);

    /** 오늘 체크인 예정 리스트 */
    @Query(value = """
        SELECT
          r.reservation_id           AS reservationId,
          COALESCE(r.guest_name,'-') AS guestName,
          a.name                     AS accommodationName,
          rm.name                    AS roomName,
          r.check_in_date                 AS checkIn,
          DATEDIFF(r.check_out_date, r.check_in_date) AS nights
        FROM reservation r
        JOIN accommodation a ON a.accommodation_id = r.accommodation_id
        LEFT JOIN room rm ON rm.room_id = r.room_id
        WHERE r.accommodation_id IN (:accIds)
          AND r.status IN ('CONFIRMED')
          AND r.check_in_date = :today
        ORDER BY a.name, rm.name
        """, nativeQuery = true)
    List<Object[]> findTodayCheckins(@Param("accIds") List<Long> accommodationIds,
                                     @Param("today") LocalDate today);
}

