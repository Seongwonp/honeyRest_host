package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.honeyrest_host.entity.PriceCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OPriceCalendarRepository extends JpaRepository<PriceCalendar, Long> {
    List<PriceCalendar> findByRoom_RoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);

}
