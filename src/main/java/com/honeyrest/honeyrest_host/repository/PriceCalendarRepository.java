package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.PriceCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public interface PriceCalendarRepository extends JpaRepository<PriceCalendar, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update PriceCalendar pc
           set pc.price = COALESCE(:price, pc.price),
               pc.availableRoom = COALESCE(:available, pc.availableRoom)
         where pc.room.roomId = :roomId and pc.date = :date
    """)
    int updateValues(@Param("roomId") Long roomId,
                     @Param("date") LocalDate date,
                     @Param("price") BigDecimal price,
                     @Param("available") Integer available);

    // 월 범위 조회 (roomId는 서비스에서 필터링하므로 null로 넘겨 전체 불러와도 됨)
    @Query("""
        select pc from PriceCalendar pc
        join fetch pc.room r
        where pc.date between :start and :end
          and (:roomId is null or r.roomId = :roomId)
    """)
    List<PriceCalendar> findMonth(@Param("start") LocalDate start,
                                  @Param("end") LocalDate end,
                                  @Param("roomId") Long roomId);

    // MariaDB ON DUPLICATE KEY: (room_id, date) 유니크 인덱스 필요
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        INSERT INTO price_calendar (room_id, date, price, available_room, created_at, updated_at)
        VALUES (:roomId, :date, :price, :available, NOW(), NOW())
        ON DUPLICATE KEY UPDATE
          price = COALESCE(VALUES(price), price),
          available_room = COALESCE(VALUES(available_room), available_room),
          updated_at = NOW()
    """, nativeQuery = true)
    int upsertMaria(@Param("roomId") Long roomId,
                    @Param("date") LocalDate date,
                    @Param("price") BigDecimal price,
                    @Param("available") Integer available);
}