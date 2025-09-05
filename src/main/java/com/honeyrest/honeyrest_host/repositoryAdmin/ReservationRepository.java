package com.honeyrest.honeyrest_host.repositoryAdmin;


import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection.SalesStatRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 예약번호로 단건 예약 찾기
    Optional<Reservation> findByReservationNumber(String number);

    // 예약 상태에 따라 예약 목록 페이징
    Page<Reservation> findByStatus(String status, Pageable pageable);

    // 기간과 "겹치는" 예약들 조회 (체크인 < ednDate and 체크아웃 > startDate) -> 구간 겹침)
    @Query("""
                select r
                  from Reservation r
                 where r.room.roomId = :roomId
                   and r.checkInDate < :endDate
                   and r.checkOutDate > :startDate
            """)
    List<Reservation> findByRoomIdAndDateBetween(@Param("roomId") Long roomId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);


    // 회사별 예약 목록 (검색/상태 필터) ->예약 목록 페이지(검색창 + 상태 필터)
    @Query("""
            select r
            from Reservation r
            join r.room rm
            join rm.accommodation acc
            where acc.company.companyId = :companyId
              and (:status is null or r.status = :status)
              and (
                 :q is null or :q = '' or
                 r.reservationNumber like concat('%', :q, '%') or
                 r.guestName like concat('%', :q, '%') or
                 r.guestPhone like concat('%', :q, '%')
              )
            """)
    Page<Reservation> findCompanyReservations(@Param("companyId") Long companyId,
                                              @Param("status") String status,
                                              @Param("q") String q,
                                              Pageable pageable);

    // 전체 예약 중 취소되지 않은 예약 개수 ->전체  대시보드 통계용
    @Query("""
                select count(r)
                from Reservation r
                where r.status <> 'CANCELLED'
            """)
    long countActiveAll();

    // 회사별(Company) 취소 되지 않은 예약 . -> 회사별 대시보드 (내 예약이 몇건 있는지 알기 위함)
    @Query("""
                select count(r)
                from Reservation r
                join r.room rm
                join rm.accommodation a
                join a.company c
                where c.companyId = :companyId
                  and r.status <> 'CANCELLED'
            """)
    long countActiveByCompanyId(@Param("companyId") Long companyId);

    // 회사(or 숙소) 객실의 월 범위에 걸친 예약들 싸그리
    // 체크인/ 체크아웃 날짜가 그달과 겹치는 모든 예약 포함. -> 월단위 캘린더 재고/예약 표시
    @Query("""
                select r
                from Reservation r
                join r.room rm
                join rm.accommodation a
                where a.company.companyId = :companyId
                  and (:accommodationId is null or a.accommodationId = :accommodationId)
                  and r.status <> 'CANCELLED'
                  and (
                       r.checkInDate <= :endDate
                   and r.checkOutDate >  :startDate
                  )
            """)
    List<Reservation> findOverlappedReservationsForMonth(@Param("companyId") Long companyId,
                                                         @Param("accommodationId") Long accommodationId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);


    @Query("""
            select r from Reservation r
            join r.accommodation a
            join a.company c
            where c.companyId = :companyId
              and r.status = 'CANCEL_REQUEST'
              and (:q is null or :q = '' or
                   r.reservationNumber like concat('%',:q,'%')
                   or r.guestName like concat('%',:q,'%')
                   or r.guestPhone like concat('%',:q,'%'))
            """)
    Page<Reservation> findCancelRequestsByCompanyViaAcc(
            @Param("companyId") Long companyId,
            @Param("q") String q,
            Pageable pageable);


    @Query("""
                SELECT r
                FROM Reservation r
                JOIN r.room rm
                JOIN rm.accommodation a
                JOIN a.company c
                WHERE c.companyId = :companyId
                  AND (:status IS NULL OR :status = 'ALL' OR UPPER(r.status) = UPPER(:status))
                  AND (:accId IS NULL OR a.accommodationId = :accId)
                  AND (
                       :q IS NULL OR :q = '' OR
                       r.reservationNumber LIKE CONCAT('%', :q, '%') OR
                       r.guestName        LIKE CONCAT('%', :q, '%') OR
                       r.roomName         LIKE CONCAT('%', :q, '%')
                  )
            """)
    Page<Reservation> searchCompanyReservations(
            @Param("companyId") Long companyId,
            @Param("status") String status,
            @Param("q") String q,
            @Param("accId") Long accId,
            Pageable pageable);


    // 취소 승인
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update Reservation r
                   set r.status = 'CANCELLED',
                       r.cancelReason = :reason,
                       r.updatedAt = :now
                 where r.reservationId = :id
                   and r.status = 'CANCEL_REQUEST'
            """)
    int approveCancelRequest(@Param("id") Long id, @Param("reason") String reason, @Param("now") LocalDate now);

    // 취소 거부(확정으로 복귀)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update Reservation r
                   set r.status = 'CONFIRMED',
                       r.cancelReason = concat('[REJECT] ', coalesce(:reason,'')),
                       r.updatedAt = :now
                 where r.reservationId = :id
                   and r.status = 'CANCEL_REQUEST'
            """)
    int rejectCancelRequest(@Param("id") Long id, @Param("reason") String reason, @Param("now") LocalDate now);

    // 체크아웃 완료 -> COMPLETED
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update Reservation r
                   set r.status = 'COMPLETED',
                       r.updatedAt = :now
                 where r.reservationId = :id
                   and r.status = 'CONFIRMED'
            """)
    int markCompleted(@Param("id") Long id, @Param("now") LocalDate now);

    // 노쇼 처리 -> NO_SHOW
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update Reservation r
                   set r.status = 'NO_SHOW',
                       r.updatedAt = :now
                 where r.reservationId = :id
                   and r.status in ('PENDING','CONFIRMED')
            """)
    int markNoShow(@Param("id") Long id, @Param("now") LocalDate now);

    // 회사 기준 체크인 예정 (예: CONFIRMED/PAID)
    @Query(value = """
            SELECT 
                r.reservation_id,
                r.guest_name,
                a.name AS accommodation_name,
                rm.name AS room_name,
                r.check_in_date,
                DATEDIFF(r.check_out_date, r.check_in_date) AS nights
            FROM reservation r
            JOIN accommodation a ON a.accommodation_id = r.accommodation_id
            LEFT JOIN room rm ON rm.room_id = r.room_id
            WHERE a.company_id = :companyId
              AND r.status IN ('CONFIRMED','PAID')
              AND DATE(r.check_in_date) = :date
            ORDER BY r.check_in_date ASC
            LIMIT :size
            """, nativeQuery = true)
    List<Object[]> findUpcomingCheckins(@Param("companyId") Long companyId,
                                        @Param("date") LocalDate date,
                                        @Param("size") int size);

    // 전체/취소 건수 요약
    @Query(value = """
            SELECT 
                SUM(CASE WHEN r.status IN ('CANCELED','REFUNDED') THEN 1 ELSE 0 END) AS canceled,
                COUNT(*) AS total
            FROM reservation r
            JOIN accommodation a ON a.accommodation_id = r.accommodation_id
            WHERE a.company_id = :companyId
              AND DATE(r.created_at) BETWEEN :from AND :to
            """, nativeQuery = true)
    Object[] findCancelSummary(@Param("companyId") Long companyId,
                               @Param("from") LocalDate from,
                               @Param("to") LocalDate to);

    // 기간과 겹치는 판매박수(상태 기준은 정책에 맞게 조정)
    @Query(value = """
            SELECT COALESCE(SUM(
                     GREATEST(0,
                       DATEDIFF(
                         LEAST(r.check_out_date, DATE_ADD(:to, INTERVAL 1 DAY)),
                         GREATEST(r.check_in_date, :from)
                       )
                     )
                   ), 0) AS soldNights
            FROM reservation r
            JOIN accommodation a ON a.accommodation_id = r.accommodation_id
            WHERE a.company_id = :companyId
              AND r.status IN ('CONFIRMED','PAID','CHECKED_IN','CHECKED_OUT')
              AND r.check_in_date < DATE_ADD(:to, INTERVAL 1 DAY)
              AND r.check_out_date > :from
            """, nativeQuery = true)
    Integer calcSoldNights(@Param("companyId") Long companyId,
                           @Param("from") LocalDate from,
                           @Param("to") LocalDate to);


    // 일별
    @Query(value = """
                SELECT DATE(r.check_in_date) AS bucket,
                       COALESCE(SUM(r.price),0) AS totalSales,
                       COUNT(*) AS totalOrders,
                       CASE WHEN COUNT(*)=0 THEN 0 ELSE SUM(r.price)/COUNT(*) END AS avgOrderPrice,
                       NULL AS dayOfWeek
                FROM reservation r
                WHERE r.status IN ('CONFIRMED','COMPLETED')
                  AND r.check_in_date BETWEEN :from AND :to
                GROUP BY DATE(r.check_in_date)
                ORDER BY bucket
            """, nativeQuery = true)
    List<SalesStatRow> findDailyReservationSales(@Param("from") LocalDate from, @Param("to") LocalDate to);

    // 주별
    @Query(value = """
                SELECT DATE(DATE_SUB(r.check_in_date, INTERVAL WEEKDAY(r.check_in_date) DAY)) AS bucket,
                       COALESCE(SUM(r.price),0) AS totalSales,
                       COUNT(*) AS totalOrders,
                       NULL AS avgOrderPrice,
                       NULL AS dayOfWeek
                FROM reservation r
                WHERE r.status IN ('CONFIRMED','COMPLETED')
                  AND r.check_in_date BETWEEN :from AND :to
                GROUP BY DATE(DATE_SUB(r.check_in_date, INTERVAL WEEKDAY(r.check_in_date) DAY))
                ORDER BY bucket
            """, nativeQuery = true)
    List<SalesStatRow> findWeeklyReservationSales(@Param("from") LocalDate from, @Param("to") LocalDate to);

    // 월별
    @Query(value = """
                SELECT DATE_FORMAT(r.check_in_date,'%Y-%m-01') AS bucket,
                       COALESCE(SUM(r.price),0) AS totalSales,
                       COUNT(*) AS totalOrders,
                       NULL AS avgOrderPrice,
                       NULL AS dayOfWeek
                FROM reservation r
                WHERE r.status IN ('CONFIRMED','COMPLETED')
                  AND r.check_in_date BETWEEN :from AND :to
                GROUP BY DATE_FORMAT(r.check_in_date,'%Y-%m')
                ORDER BY bucket
            """, nativeQuery = true)
    List<SalesStatRow> findMonthlyReservationSales(@Param("from") LocalDate from, @Param("to") LocalDate to);

    // 요일별
    @Query(value = """
                SELECT DATE(r.check_in_date) AS bucket,
                       COALESCE(SUM(r.price),0) AS totalSales,
                       COUNT(*) AS totalOrders,
                       NULL AS avgOrderPrice,
                       (CASE WHEN DAYOFWEEK(r.check_in_date)=1 THEN 7 ELSE DAYOFWEEK(r.check_in_date)-1 END) AS dayOfWeek
                FROM reservation r
                WHERE r.status IN ('CONFIRMED','COMPLETED')
                  AND r.check_in_date BETWEEN :from AND :to
                GROUP BY dayOfWeek
                ORDER BY dayOfWeek
            """, nativeQuery = true)
    List<SalesStatRow> findWeekdayReservationSales(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Modifying(clearAutomatically = true, flushAutomatically = true)


    // 체크인일 기준, 회사(+선택 숙소/객실) 필터
    @Query("""
    select r
      from Reservation r
      join r.room rm
      join rm.accommodation a
     where a.company.companyId = :companyId
       and (:accommodationId is null or a.accommodationId = :accommodationId)
       and (:roomId is null or rm.roomId = :roomId)
       and r.status in ('CONFIRMED','PENDING','CENCEL_REQUEST','NO_SHOW','COMPLETED')
       and r.checkInDate between :startDate and :endDate
""")
    List<Reservation> findCheckinsForRange(@Param("companyId") Long companyId,
                                           @Param("accommodationId") Long accommodationId,
                                           @Param("roomId") Long roomId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);



    // 숙소/회사 일별 매출 합계만 바로 뽑는 버전(그룹바이)
    @Query(value = """
    SELECT DATE(r.check_in_date) AS bucket,
           COALESCE(SUM(r.price),0) AS totalSales
      FROM reservation r
      JOIN accommodation a ON a.accommodation_id = r.accommodation_id
     WHERE a.company_id = :companyId
       AND (:accommodationId IS NULL OR a.accommodation_id = :accommodationId)
       AND r.status IN ('CONFIRMED','PAID','CHECKED_IN','CHECKED_OUT','COMPLETED')
       AND DATE(r.check_in_date) BETWEEN :start AND :end
     GROUP BY DATE(r.check_in_date)
     ORDER BY bucket
""", nativeQuery = true)
    List<Object[]> sumDailyRevenueByCheckin(@Param("companyId") Long companyId,
                                            @Param("accommodationId") Long accommodationId,
                                            @Param("start") LocalDate start,
                                            @Param("end") LocalDate end);



}



