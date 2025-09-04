package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection.*;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository <Accommodation, Long> {
    /* ========== 일 단위 매출 (payment 기준) ========== */
    @Query(value = """
        SELECT 
          DATE(p.payment_date)                                   AS bucket,
          COALESCE(SUM(p.amount),0)                              AS totalSales,
          COUNT(DISTINCT p.payment_id)                           AS totalOrders,
          CASE WHEN COUNT(*)=0 THEN 0 ELSE COALESCE(SUM(p.amount),0)/COUNT(*) END AS avgOrderPrice,
          r.accommodation_id                                     AS accommodationId,
          r.accommodation_name                                   AS accommodationName
        FROM payment p
        JOIN reservation r ON r.reservation_id = p.reservation_id
        WHERE p.payment_date >= :from
          AND p.payment_date < DATE_ADD(:to, INTERVAL 1 DAY)
          AND p.payment_status = 'COMPLETED'
          AND r.status IN ('CONFIRMED','COMPLETED')
        GROUP BY DATE(p.payment_date), r.accommodation_id, r.accommodation_name
        ORDER BY bucket ASC
    """, nativeQuery = true)
    List<DailySalesRow> findDailySales(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    /* ========== 주 단위 (주 시작일: 월요일) ========== */
    @Query(value = """
        SELECT 
          DATE_FORMAT(DATE_SUB(DATE(p.payment_date), INTERVAL (WEEKDAY(p.payment_date)) DAY), '%Y-%m-%d') AS bucket,
          COALESCE(SUM(p.amount),0)  AS totalSales,
          COUNT(DISTINCT p.payment_id) AS totalOrders,
          NULL AS dayOfWeek,
          NULL AS salesDiff
        FROM payment p
        JOIN reservation r ON r.reservation_id = p.reservation_id
        WHERE p.payment_date >= :from
          AND p.payment_date < DATE_ADD(:to, INTERVAL 1 DAY)
          AND p.payment_status = 'COMPLETED'
          AND r.status IN ('CONFIRMED','COMPLETED')
        GROUP BY DATE_SUB(DATE(p.payment_date), INTERVAL (WEEKDAY(p.payment_date)) DAY)
        ORDER BY bucket
    """, nativeQuery = true)
    List<SalesStatRow> findWeeklyStats(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    /* ========== 월 단위 ========== */
    @Query(value = """
        SELECT 
          DATE_FORMAT(p.payment_date, '%Y-%m') AS bucket,
          COALESCE(SUM(p.amount),0)            AS totalSales,
          COUNT(DISTINCT p.payment_id)         AS totalOrders,
          NULL AS dayOfWeek,
          NULL AS salesDiff
        FROM payment p
        JOIN reservation r ON r.reservation_id = p.reservation_id
        WHERE p.payment_date >= :from
          AND p.payment_date < DATE_ADD(:to, INTERVAL 1 DAY)
          AND p.payment_status = 'COMPLETED'
          AND r.status IN ('CONFIRMED','COMPLETED')
        GROUP BY DATE_FORMAT(p.payment_date, '%Y-%m')
        ORDER BY bucket
    """, nativeQuery = true)
    List<SalesStatRow> findMonthlyStats(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    /* ========== 요일 단위 (1=월 ~ 7=일) ========== */
    @Query(value = """
        SELECT 
          CASE WHEN DAYOFWEEK(p.payment_date)=1 THEN 'Sunday' ELSE DATE_FORMAT(p.payment_date, '%W') END AS bucket,
          COALESCE(SUM(p.amount),0)  AS totalSales,
          COUNT(DISTINCT p.payment_id) AS totalOrders,
          (CASE WHEN DAYOFWEEK(p.payment_date)=1 THEN 7 ELSE DAYOFWEEK(p.payment_date)-1 END) AS dayOfWeek,
          NULL AS salesDiff
        FROM payment p
        JOIN reservation r ON r.reservation_id = p.reservation_id
        WHERE p.payment_date >= :from
          AND p.payment_date < DATE_ADD(:to, INTERVAL 1 DAY)
          AND p.payment_status = 'COMPLETED'
          AND r.status IN ('CONFIRMED','COMPLETED')
        GROUP BY (CASE WHEN DAYOFWEEK(p.payment_date)=1 THEN 7 ELSE DAYOFWEEK(p.payment_date)-1 END), bucket
        ORDER BY dayOfWeek
    """, nativeQuery = true)
    List<SalesStatRow> findWeekdayStats(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    /* ========== 매출 TOP 숙소 ========== */
    @Query(value = """
        SELECT 
          r.accommodation_id             AS accommodationId,
          r.accommodation_name           AS accommodationName,
          COALESCE(SUM(p.amount),0)      AS totalSales
        FROM payment p
        JOIN reservation r ON r.reservation_id = p.reservation_id
        WHERE p.payment_date >= :from
          AND p.payment_date < DATE_ADD(:to, INTERVAL 1 DAY)
          AND p.payment_status = 'COMPLETED'
          AND r.status IN ('CONFIRMED','COMPLETED')
        GROUP BY r.accommodation_id, r.accommodation_name
        ORDER BY totalSales DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<TopAccommodationRow> findTopAccommodations(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("limit") int limit
    );

    /* ========== 취소 요약 ========== */
    @Query(value = """
        SELECT
          (SELECT COUNT(*) FROM reservation r
           WHERE r.created_at >= :from
             AND r.created_at < DATE_ADD(:to, INTERVAL 1 DAY)) AS total,
          (SELECT COUNT(*) FROM reservation r
           WHERE r.updated_at >= :from
             AND r.updated_at < DATE_ADD(:to, INTERVAL 1 DAY)
             AND r.status = 'CANCELED') AS canceled
    """, nativeQuery = true)
    CancelSummaryRow findCancelSummary(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    /* ========== 점유율(판매박수/가용박수/ADR/RevPAR) ========== */
    @Query(value = """
        WITH sold AS (
          SELECT 
            COALESCE(SUM(DATEDIFF(r.check_out_date, r.check_in_date)),0) AS soldNights,
            COALESCE(SUM(p.amount),0)                                    AS totalSales
          FROM reservation r
          LEFT JOIN payment p ON p.reservation_id = r.reservation_id
                              AND p.payment_status = 'COMPLETED'
          WHERE r.check_in_date  < DATE_ADD(:to, INTERVAL 1 DAY)
            AND r.check_out_date > :from
            AND r.status IN ('CONFIRMED','COMPLETED')
        ),
        avail AS (
          SELECT COALESCE(SUM(rm.total_rooms),0) AS totalRooms
          FROM room rm
        )
        SELECT 
          s.soldNights                        AS soldNights,
          (a.totalRooms * DATEDIFF(DATE_ADD(:to, INTERVAL 1 DAY), :from)) AS availableNights,
          CASE WHEN s.soldNights=0 THEN 0 ELSE s.totalSales / s.soldNights END AS adr,
          CASE WHEN (a.totalRooms * DATEDIFF(DATE_ADD(:to, INTERVAL 1 DAY), :from))=0 
               THEN 0 
               ELSE s.totalSales / (a.totalRooms * DATEDIFF(DATE_ADD(:to, INTERVAL 1 DAY), :from)) END AS revpar
        FROM sold s CROSS JOIN avail a
    """, nativeQuery = true)
    OccupancyRow findOccupancy(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    /* ========== 다가오는 체크인 ========== */
    @Query(value = """
        SELECT 
          r.reservation_id                                  AS reservationId,
          r.guest_name                                      AS guestName,
          r.accommodation_name                              AS accommodationName,
          r.room_name                                       AS roomName,
          r.check_in_date                                   AS checkIn,
          DATEDIFF(r.check_out_date, r.check_in_date)       AS nights
        FROM reservation r
        WHERE r.check_in_date BETWEEN :from AND :to
          AND r.status IN ('DONE')
        ORDER BY r.check_in_date ASC
        LIMIT :limit
    """, nativeQuery = true)
    List<UpcomingCheckinRow> findUpcomingCheckins(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("limit") int limit
    );
}

