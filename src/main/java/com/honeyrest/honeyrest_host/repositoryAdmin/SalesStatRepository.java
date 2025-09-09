package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;

public interface SalesStatRepository extends JpaRepository<Payment, Long> {

    /* ────────────────────────────
       결제 기준: 일별 집계
       r[0]=Date(yyyy-MM-dd), r[1]=totalSales(BigDecimal),
       r[2]=totalOrders(Number), r[3]=avgOrderPrice(BigDecimal)
       ──────────────────────────── */
    @Query(value = """
        SELECT DATE(p.payment_date)                                  AS bucket,
               COALESCE(SUM(p.amount), 0)                            AS totalSales,
               COUNT(DISTINCT r.reservation_id)                      AS totalOrders,
               CASE WHEN COUNT(DISTINCT r.reservation_id)=0 THEN 0
                    ELSE COALESCE(SUM(p.amount),0)/COUNT(DISTINCT r.reservation_id) END AS avgOrderPrice
          FROM payment p
          JOIN reservation r ON r.reservation_id = p.reservation_id
         WHERE r.accommodation_id IN (:accIds)
           AND p.payment_status = 'DONE'
           AND p.payment_date  >= :from
           AND p.payment_date  <  DATE_ADD(:to, INTERVAL 1 DAY)
         GROUP BY DATE(p.payment_date)
         ORDER BY bucket
        """, nativeQuery = true)
    List<Object[]> findDaily(@Param("accIds") List<Long> accIds,
                             @Param("from") LocalDate from,
                             @Param("to")   LocalDate to);

    /* ────────────────────────────
       결제 기준: 주별 집계(대표일 = 월요일)
       r[0]=Date(yyyy-MM-dd 월요일), r[1]=totalSales, r[2]=totalOrders, r[3]=NULL
       ──────────────────────────── */
    @Query(value = """
        SELECT DATE(DATE_SUB(p.payment_date, INTERVAL WEEKDAY(p.payment_date) DAY)) AS bucket,
               COALESCE(SUM(p.amount), 0)                                          AS totalSales,
               COUNT(DISTINCT r.reservation_id)                                     AS totalOrders,
               NULL                                                                  AS avgOrderPrice
          FROM payment p
          JOIN reservation r ON r.reservation_id = p.reservation_id
         WHERE r.accommodation_id IN (:accIds)
           AND p.payment_status = 'DONE'
           AND p.payment_date  >= :from
           AND p.payment_date  <  DATE_ADD(:to, INTERVAL 1 DAY)
         GROUP BY DATE(DATE_SUB(p.payment_date, INTERVAL WEEKDAY(p.payment_date) DAY))
         ORDER BY bucket
        """, nativeQuery = true)
    List<Object[]> findWeekly(@Param("accIds") List<Long> accIds,
                              @Param("from") LocalDate from,
                              @Param("to")   LocalDate to);

    /* ────────────────────────────
       결제 기준: 월별 집계
       r[0]=String("yyyy-MM"), r[1]=totalSales
       ──────────────────────────── */
    @Query(value = """
        SELECT DATE_FORMAT(p.payment_date, '%Y-%m') AS ym,
               COALESCE(SUM(p.amount), 0)          AS totalSales
          FROM payment p
          JOIN reservation r ON r.reservation_id = p.reservation_id
         WHERE r.accommodation_id IN (:accIds)
           AND p.payment_status = 'DONE'
           AND p.payment_date  >= :from
           AND p.payment_date  <  DATE_ADD(:to, INTERVAL 1 DAY)
         GROUP BY DATE_FORMAT(p.payment_date, '%Y-%m')
         ORDER BY ym
        """, nativeQuery = true)
    List<Object[]> findMonthly(@Param("accIds") List<Long> accIds,
                               @Param("from") LocalDate from,
                               @Param("to")   LocalDate to);

    /* ────────────────────────────
       결제 기준: 요일별 집계 (월=1 … 일=7)
       r[0]=Integer(dow), r[1]=totalSales, r[2]=totalOrders, r[3]=NULL
       ──────────────────────────── */
    @Query(value = """
        SELECT (CASE WHEN DAYOFWEEK(p.payment_date)=1 THEN 7 ELSE DAYOFWEEK(p.payment_date)-1 END) AS dow,
               COALESCE(SUM(p.amount), 0)                                                           AS totalSales,
               COUNT(DISTINCT r.reservation_id)                                                     AS totalOrders,
               NULL                                                                                 AS avgOrderPrice
          FROM payment p
          JOIN reservation r ON r.reservation_id = p.reservation_id
         WHERE r.accommodation_id IN (:accIds)
           AND p.payment_status = 'DONE'
           AND p.payment_date  >= :from
           AND p.payment_date  <  DATE_ADD(:to, INTERVAL 1 DAY)
         GROUP BY dow
         ORDER BY dow
        """, nativeQuery = true)
    List<Object[]> findWeekday(@Param("accIds") List<Long> accIds,
                               @Param("from") LocalDate from,
                               @Param("to")   LocalDate to);

    /* ────────────────────────────
       대시보드: Top-N 객실 (최근 구간 합계)
       r = [roomId, roomName, accommodationName, totalOrders, totalSales]
       ──────────────────────────── */
    @Query(value = """
        SELECT rm.room_id                        AS roomId,
               rm.name                           AS roomName,
               a.name                            AS accommodationName,
               COUNT(DISTINCT r.reservation_id)  AS totalOrders,
               COALESCE(SUM(p.amount), 0)        AS totalSales
          FROM payment p
          JOIN reservation   r  ON r.reservation_id   = p.reservation_id
          JOIN room          rm ON rm.room_id         = r.room_id
          JOIN accommodation a  ON a.accommodation_id = r.accommodation_id
         WHERE r.accommodation_id IN (:accIds)
           AND p.payment_status = 'DONE'
           AND p.payment_date  >= :from
           AND p.payment_date  <  DATE_ADD(:to, INTERVAL 1 DAY)
         GROUP BY rm.room_id, rm.name, a.name
         ORDER BY totalSales DESC
         LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopRoomsByAccommodations(@Param("accIds") List<Long> accIds,
                                                @Param("from") LocalDate from,
                                                @Param("to")   LocalDate to,
                                                @Param("limit") int limit);
}