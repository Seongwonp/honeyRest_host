package com.honeyrest.honeyrest_host.repositoryAdmin;


import com.honeyrest.domain.entity.Reservation;
import com.honeyrest.domain.type.ReservationStatus;
import com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection.SalesStatRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.Collection;
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
    // 재고 계산과 같은 기준으로 점유 상태만 포함
    default List<Reservation> findOverlappedReservationsForMonth(Integer companyId,
                                                                 Long accommodationId,
                                                                 LocalDate startDate,
                                                                 LocalDate endDate) {
        return findOverlappedReservationsForMonthByStatuses(
                companyId, accommodationId, startDate, endDate, ReservationStatus.OCCUPYING);
    }

    @Query("""
                select r
                from Reservation r
                join fetch r.room rm
                join fetch rm.accommodation a
                where a.company.companyId = :companyId
                  and (:accommodationId is null or a.accommodationId = :accommodationId)
                  and r.status in :statuses
                  and (
                       r.checkInDate <= :endDate
                   and r.checkOutDate >  :startDate
                  )
            """)
    List<Reservation> findOverlappedReservationsForMonthByStatuses(@Param("companyId") Integer companyId,
                                                                   @Param("accommodationId") Long accommodationId,
                                                                   @Param("startDate") LocalDate startDate,
                                                                   @Param("endDate") LocalDate endDate,
                                                                   @Param("statuses") Collection<String> statuses);

    /**
     * [checkIn, checkOut) 과 겹치는 점유 예약 수를 잠금 읽기(FOR UPDATE)로 센다. 스냅샷 읽기는 락 대기 중 커밋된 예약을 놓친다.
     * H2 가 COUNT(*) ... FOR UPDATE 를 지원하지 않아 ID 를 잠그며 읽고 센다. excludeId 는 수정 시 자기 자신 제외용.
     * 사용자 API ReservationRepository.countOverlapping 과 같은 규칙을 유지할 것.
     */
    default long countOverlappingForUpdate(Long roomId, LocalDate checkIn, LocalDate checkOut,
                                           Collection<String> statuses, Long excludeId) {
        // 네이티브 쿼리의 null 타입 추론 문제를 피하려고 "제외 없음"은 -1 로 넘긴다
        return findOverlappingIdsForUpdate(roomId, checkIn, checkOut, statuses,
                excludeId == null ? -1L : excludeId).size();
    }

    @Query(value = """
                SELECT r.reservation_id
                  FROM reservation r
                 WHERE r.room_id = :roomId
                   AND r.status IN (:statuses)
                   AND r.check_in_date < :checkOut
                   AND r.check_out_date > :checkIn
                   AND r.reservation_id <> :excludeId
                 FOR UPDATE
            """, nativeQuery = true)
    List<Long> findOverlappingIdsForUpdate(@Param("roomId") Long roomId,
                                           @Param("checkIn") LocalDate checkIn,
                                           @Param("checkOut") LocalDate checkOut,
                                           @Param("statuses") Collection<String> statuses,
                                           @Param("excludeId") Long excludeId);

    /**
     * 예약 행을 잠그고 읽는다. 같은 예약의 상태 전이·환불을 직렬화하고 이후 재고 검사가 옛 스냅샷을 보지 않게 한다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.reservationId = :id")
    Optional<Reservation> findByIdForUpdate(@Param("id") Long id);


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

    boolean existsByRoom_RoomId(Long roomId);

    @Query("""
        select count(r) from Reservation r
        join r.accommodation a
        where a.accommodationId in :accIds
          and r.status = 'CANCEL_REQUEST'
    """)
    long countCancelRequestByAccommodationIds(@Param("accIds") java.util.List<Long> accIds);

}



