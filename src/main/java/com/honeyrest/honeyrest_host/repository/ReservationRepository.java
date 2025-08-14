package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 예약 번호로 조회
    Optional<Reservation> findByReservationNumber(String number);

    // 예약 현황으로 예약 목록 조회
    Page<Reservation> findByStatus(String status, Pageable pageable);

    // 예약 전체
    Page<Reservation> findAll(Pageable pageable);


}
