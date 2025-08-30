package com.honeyrest.honeyrest_host.repository;


import com.honeyrest.honeyrest_host.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}

