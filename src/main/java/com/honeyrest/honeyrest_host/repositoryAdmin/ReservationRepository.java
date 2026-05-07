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
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 예약번호로 단건 예약 찾기
    Optional<Reservation> findByReservationNumber(String number);

    // 예약 상태에 따라 예약 목록 페이징
    Page<Reservation> findByStatus(String status, Pageable pageable);

    // 기간과 "겹치는" 예약들 조회 (체크인 < endDate and 체크아웃 > startDate) -> 구간 겹침)
    @Query("""
                select r
                  from Reservation r
                  join fetch r.room rm
                  join fetch r.accommodation
                 where rm.roomId = :roomId
                   and r.checkInDate < :endDate
                   and r.checkOutDate > :startDate
            """)
    List<Reservation> findByRoomIdAndDateBetween(@Param("roomId") Long roomId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);


    // 회사별 예약 목록 (검색/상태 필터) ->예약 목록 페이지(검색창 + 상태 필터)
    @Query(value = """
            select r
            from Reservation r
            join fetch r.room rm
            join fetch rm.accommodation acc
            where acc.company.companyId = :companyId
              and (:status is null or r.status = :status)
              and (
                 :q is null or :q = '' or
                 r.reservationNumber like concat('%', :q, '%') or
                 r.guestName like concat('%', :q, '%') or
                 r.guestPhone like concat('%', :q, '%')
              )
            """,
            countQuery = """
            select count(r)
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
    Page<Reservation> findCompanyReservations(@Param("companyId") Integer companyId,
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
    long countActiveByCompanyId(@Param("companyId") Integer companyId);

    // 회사(or 숙소) 객실의 월 범위에 걸친 예약들 싸그리
    // 체크인/ 체크아웃 날짜가 그달과 겹치는 모든 예약 포함. -> 월단위 캘린더 재고/예약 표시
    @Query("""
                select r
                from Reservation r
                join fetch r.room rm
                join fetch rm.accommodation a
                where a.company.companyId = :companyId
                  and (:accommodationId is null or a.accommodationId = :accommodationId)
                  and r.status <> 'CANCELLED'
                  and (
                       r.checkInDate <= :endDate
                   and r.checkOutDate >  :startDate
                  )
            """)
    List<Reservation> findOverlappedReservationsForMonth(@Param("companyId") Integer companyId,
                                                         @Param("accommodationId") Long accommodationId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);



    @Query(value = """
                SELECT r
                FROM Reservation r
                JOIN FETCH r.room rm
                JOIN FETCH rm.accommodation a
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
            """,
            countQuery = """
                SELECT count(r)
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
            @Param("companyId") Integer companyId,
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
    int approveCancelRequest(@Param("id") Long id, @Param("reason") String reason, @Param("now") LocalDateTime now);

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
    int rejectCancelRequest(@Param("id") Long id, @Param("reason") String reason, @Param("now") LocalDateTime now);

    // 체크아웃 완료 -> COMPLETED
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update Reservation r
                   set r.status = 'COMPLETED',
                       r.updatedAt = :now
                 where r.reservationId = :id
                   and r.status = 'CONFIRMED'
            """)
    int markCompleted(@Param("id") Long id, @Param("now") LocalDateTime now);

    // 노쇼 처리 -> NO_SHOW
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update Reservation r
                   set r.status = 'NO_SHOW',
                       r.updatedAt = :now
                 where r.reservationId = :id
                   and r.status in ('PENDING','CONFIRMED')
            """)
    int markNoShow(@Param("id") Long id, @Param("now") LocalDateTime now);

    // 체크인일 기준, 회사(+선택 숙소/객실) 필터
    @Query("""
    select r
      from Reservation r
      join fetch r.room rm
      join fetch rm.accommodation a
     where a.company.companyId = :companyId
       and (:accommodationId is null or a.accommodationId = :accommodationId)
       and (:roomId is null or rm.roomId = :roomId)
       and r.status in ('CONFIRMED','PENDING','CANCEL_REQUEST','NO_SHOW','COMPLETED')
       and r.checkInDate between :startDate and :endDate
""")
    List<Reservation> findCheckinsForRange(@Param("companyId") Integer companyId,
                                           @Param("accommodationId") Long accommodationId,
                                           @Param("roomId") Long roomId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    boolean existsByRoom_Accommodation_AccommodationId(Long accommodationId);

    @Query("""
        select count(r) from Reservation r
        join r.accommodation a
        where a.accommodationId in :accIds
          and r.status = 'CANCEL_REQUEST'
    """)
    long countCancelRequestByAccommodationIds(@Param("accIds") java.util.List<Long> accIds);

}



