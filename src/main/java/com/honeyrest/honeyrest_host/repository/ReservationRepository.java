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
    Optional<Reservation> findByReservationNumber(String number);

    Page<Reservation> findByStatus(String status, Pageable pageable);

    // 회사별 예약 목록 (검색/상태 필터)
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

    // 취소 제외 전체 예약 수
    @Query("""
        select count(r)
        from Reservation r
        where r.status <> 'CANCELLED'
    """)
    long countActiveAll();

    // 회사별(Company) 취소 제외 예약 수
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

