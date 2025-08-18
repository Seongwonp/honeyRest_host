package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
