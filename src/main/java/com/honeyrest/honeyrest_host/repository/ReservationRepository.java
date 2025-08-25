package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Company;
import com.honeyrest.honeyrest_host.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findReservationsByAccommodation_AccommodationId(Long accommodationId);

    List<Reservation> findReservationsByAccommodation_Company_CompanyId(Long CompanyId);
}
