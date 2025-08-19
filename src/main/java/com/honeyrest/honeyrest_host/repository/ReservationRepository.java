package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.entity.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

//    // 예약 번호로 조회
//    Optional<Reservation> findByReservationNumber(String number);
//
//    // 예약 현황으로 예약 목록 조회
//    Page<Reservation> findByStatus(String status, Pageable pageable);
//
//    // 예약 전체
//    Page<Reservation> findAll(Pageable pageable);

    Optional<Reservation> findByReservationNumber(String number);
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

    @Query("""
      select r from Reservation r
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
                                              @Param("status") ReservationStatus status,
                                              @Param("q") String q,
                                              Pageable pageable);
}

