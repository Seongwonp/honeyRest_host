package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection.*;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepository extends JpaRepository <Accommodation, Long> {
    /* ========== 일 단위 매출 ========== */
    @Query(value = """
        SELECT DATE_FORMAT(r.paid_at, '%Y-%m-%d')                                           AS bucket,
               COALESCE(SUM(r.total_price),0)                                              AS totalSales,
               COUNT(*)                                                                     AS totalOrders,
               CASE WHEN COUNT(*)=0 THEN 0 ELSE COALESCE(SUM(r.total_price),0)/COUNT(*) END AS avgOrderPrice,
               a.accommodation_id                                                           AS accommodationId,
               a.name                                                                       AS accommodationName
        FROM reservations r
        JOIN accommodation a ON a.accommodation_id = r.accommodation_id
        WHERE r.paid_at >= :from
          AND r.paid_at < DATE_ADD(:to, INTERVAL 1 DAY)
          AND r.status IN ('PAID','CONFIRMED','COMPLETED')
          /* 회사 범위 제한이 있으면 a.company_id = :companyId 추가 */
          /* 특정 숙소만: AND (:accommodationId IS NULL OR r.accommodation_id = :accommodationId) */
        GROUP BY DATE(r.paid_at), a.accommodation_id, a.name
        ORDER BY bucket ASC
    """, nativeQuery = true)
    List<DailySalesRow> findDailySales(String from, String to /*, Long companyId, Long accommodationId */);

    /* ========== 주 단위 (주 시작일을 버킷) ========== */
    @Query(value = """
        SELECT DATE_FORMAT(DATE_SUB(DATE(r.paid_at), INTERVAL WEEKDAY(r.paid_at) DAY), '%Y-%m-%d') AS bucket,
               COALESCE(SUM(r.total_price),0)                                                      AS totalSales,
               COUNT(*)                                                                             AS totalOrders,
               NULL                                                                                 AS dayOfWeek,
               NULL                                                                                 AS salesDiff
        FROM reservations r
        WHERE r.paid_at >= :from
          AND r.paid_at < DATE_ADD(:to, INTERVAL 1 DAY)
          AND r.status IN ('PAID','CONFIRMED','COMPLETED')
        GROUP BY DATE_SUB(DATE(r.paid_at), INTERVAL WEEKDAY(r.paid_at) DAY)
        ORDER BY bucket
    """, nativeQuery = true)
    List<SalesStatRow> findWeeklyStats(String from, String to);

    /* ========== 월 단위 ========== */
    @Query(value = """
        SELECT DATE_FORMAT(r.paid_at, '%Y-%m') AS bucket,
               COALESCE(SUM(r.total_price),0)  AS totalSales,
               COUNT(*)                        AS totalOrders,
               NULL                            AS dayOfWeek,
               NULL                            AS salesDiff
        FROM reservations r
        WHERE r.paid_at >= :from
          AND r.paid_at < DATE_ADD(:to, INTERVAL 1 DAY)
          AND r.status IN ('PAID','CONFIRMED','COMPLETED')
        GROUP BY DATE_FORMAT(r.paid_at, '%Y-%m')
        ORDER BY bucket
    """, nativeQuery = true)
    List<SalesStatRow> findMonthlyStats(String from, String to);

    /* ========== 요일 단위(월~일) ========== */
    @Query(value = """
        SELECT DATE_FORMAT(r.paid_at, '%W')    AS bucket,     -- Monday, Tuesday ...
               COALESCE(SUM(r.total_price),0) AS totalSales,
               COUNT(*)                        AS totalOrders,
               (CASE WHEN DAYOFWEEK(r.paid_at)=1 THEN 7 ELSE DAYOFWEEK(r.paid_at)-1 END) AS dayOfWeek, -- 1=Mon..7=Sun
               NULL AS salesDiff
        FROM reservations r
        WHERE r.paid_at >= :from
          AND r.paid_at < DATE_ADD(:to, INTERVAL 1 DAY)
          AND r.status IN ('PAID','CONFIRMED','COMPLETED')
        GROUP BY (CASE WHEN DAYOFWEEK(r.paid_at)=1 THEN 7 ELSE DAYOFWEEK(r.paid_at)-1 END), DATE_FORMAT(r.paid_at, '%W')
        ORDER BY dayOfWeek
    """, nativeQuery = true)
    List<SalesStatRow> findWeekdayStats(String from, String to);

    /* ========== 상위 숙소 매출 TOP N ========== */
    @Query(value = """
        SELECT r.accommodation_id             AS accommodationId,
               a.name                         AS accommodationName,
               COALESCE(SUM(r.total_price),0) AS totalSales
        FROM reservations r
        JOIN accommodation a ON a.accommodation_id = r.accommodation_id
        WHERE r.paid_at >= :from
          AND r.paid_at < DATE_ADD(:to, INTERVAL 1 DAY)
          AND r.status IN ('PAID','CONFIRMED','COMPLETED')
        GROUP BY r.accommodation_id, a.name
        ORDER BY totalSales DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<TopAccommodationRow> findTopAccommodations(String from, String to, int limit);

    /* ========== 취소 요약 ========== */
    @Query(value = """
        SELECT
          (SELECT COUNT(*) FROM reservations r
           WHERE r.created_at >= :from
             AND r.created_at < DATE_ADD(:to, INTERVAL 1 DAY))                         AS total,
          (SELECT COUNT(*) FROM reservations r
           WHERE r.updated_at >= :from
             AND r.updated_at < DATE_ADD(:to, INTERVAL 1 DAY)
             AND r.status = 'CANCELED')                                                AS canceled
    """, nativeQuery = true)
    CancelSummaryRow findCancelSummary(String from, String to);

    /* ========== 점유율(Occupancy) ==========
       soldNights : SUM(DATEDIFF(check_out_date, check_in_date)) on 판매된 예약들
       availableNights : (지정 기간의 일수 * 전체 객실 수 합계)
       ADR: 총매출 / soldNights
       RevPAR: 총매출 / availableNights
    */
    @Query(value = """
        WITH params AS (
          SELECT :from AS f, :to AS t
        ),
        days AS (
          SELECT DATEDIFF(DATE_ADD(t, INTERVAL 1 DAY), f) AS day_count FROM params
        ),
        rooms AS (
          SELECT a.accommodation_id, COALESCE(SUM(rm.total_rooms),0) AS total_rooms
          FROM room rm
          JOIN accommodation a ON a.accommodation_id = rm.accommodation_id
          /* 회사 제한 필요 시 WHERE a.company_id = :companyId */
          GROUP BY a.accommodation_id
        ),
        sold AS (
          SELECT COALESCE(SUM(DATEDIFF(r.check_out_date, r.check_in_date)),0) AS sold_nights,
                 COALESCE(SUM(r.total_price),0)                               AS total_sales
          FROM reservations r
          WHERE r.check_in_date  < DATE_ADD(:to, INTERVAL 1 DAY)
            AND r.check_out_date > :from
            AND r.status IN ('PAID','CONFIRMED','COMPLETED')
        ),
        avail AS (
          SELECT COALESCE(SUM(rooms.total_rooms * days.day_count),0) AS available_nights
          FROM rooms, days
        )
        SELECT
          sold.sold_nights                 AS soldNights,
          avail.available_nights           AS availableNights,
          CASE WHEN sold.sold_nights=0 THEN 0 ELSE sold.total_sales / sold.sold_nights END AS adr,
          CASE WHEN avail.available_nights=0 THEN 0 ELSE sold.total_sales / avail.available_nights END AS revpar
        FROM sold, avail
    """, nativeQuery = true)
    OccupancyRow findOccupancy(String from, String to /*, Long companyId */);

    /* ========== 다가오는 체크인 목록 ========== */
    @Query(value = """
        SELECT r.id                     AS reservationId,
               r.guest_name             AS guestName,
               a.name                   AS accommodationName,
               rm.name                  AS roomName,
               r.check_in_date          AS checkIn,
               DATEDIFF(r.check_out_date, r.check_in_date) AS nights
        FROM reservations r
        JOIN accommodation a ON a.accommodation_id = r.accommodation_id
        JOIN room rm ON rm.room_id = r.room_id
        WHERE r.check_in_date BETWEEN :from AND :to
          AND r.status IN ('PAID','CONFIRMED')     -- 체크인 예정
        ORDER BY r.check_in_date ASC
        LIMIT :limit
    """, nativeQuery = true)
    List<UpcomingCheckinRow> findUpcomingCheckins(LocalDate from, LocalDate to, int limit);
}

