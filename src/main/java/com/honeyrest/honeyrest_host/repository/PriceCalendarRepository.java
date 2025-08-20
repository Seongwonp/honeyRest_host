package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.PriceCalendar;
import com.honeyrest.honeyrest_host.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PriceCalendarRepository extends JpaRepository<PriceCalendar, Long> {
    List<PriceCalendar> findByRoom_RoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);

}
