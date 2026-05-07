package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.honeyrest_host.entity.Reservation;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface OReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findReservationsByAccommodation_AccommodationId(Long accommodationId);

    List<Reservation> findReservationsByAccommodation_Company_CompanyId(Integer CompanyId);

    @Query("SELECT r FROM Reservation r WHERE r.room.roomId = :roomId " +
            "AND (r.checkInDate <= :endDate AND r.checkOutDate >= :startDate)")
    List<Reservation> findByRoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);

    Page<Reservation> findByAccommodation_AccommodationId(Long accommodationId, Pageable pageable);

    Page<Reservation> findByAccommodation_Company_CompanyId(Integer CompanyId, Pageable pageable);

    Page<Reservation> findByRoom_RoomId(Long roomId, Pageable pageable);

    // 방별, 기간별 예약 조회
    @Query("SELECT r FROM Reservation r WHERE r.room.roomId = :roomId " +
            "AND r.checkInDate <= :endDate AND r.checkOutDate >= :startDate " +
            "AND r.status != 'CANCELLED'")
    List<Reservation> findByRoomIdAndDateRange(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


}
