package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findReservationsByAccommodation_AccommodationId(Long accommodationId);

    List<Reservation> findReservationsByAccommodation_Company_CompanyId(Long CompanyId);

    @Query("SELECT r FROM Reservation r WHERE r.room.roomId = :roomId " +
            "AND (r.checkInDate <= :endDate AND r.checkOutDate >= :startDate)")
    List<Reservation> findByRoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);

    Page<Reservation> findByAccommodation_AccommodationId(Long accommodationId, Pageable pageable);

    Page<Reservation> findByAccommodation_Company_CompanyId(Long CompanyId, Pageable pageable);
}
