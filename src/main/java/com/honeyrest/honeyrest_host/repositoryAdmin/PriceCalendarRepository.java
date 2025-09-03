package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.PriceCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public interface PriceCalendarRepository extends JpaRepository<PriceCalendar, Long> {

    // 특정 방만 고르기 위해서
    List<PriceCalendar> findByRoom_RoomIdAndDateBetweenOrderByDateAsc(Long roomId, LocalDate start, LocalDate end);

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

    interface DailyAggProjection {
        java.sql.Date getDate();
        Integer getTotalRoomsSum();
        Integer getAvailableSum();
        java.math.BigDecimal getMinPrice();
        java.math.BigDecimal getMaxPrice();
    }

    interface GridCellProjection {
        Long getRoomId();
        String getRoomName();
        java.sql.Date getDate();
        java.math.BigDecimal getPrice();
        Integer getAvailable();
        Integer getTotalRooms();
    }

    // 일자별 집계
    @Query(value = """
            WITH RECURSIVE days AS (
              SELECT :start AS d
              UNION ALL
              SELECT DATE_ADD(d, INTERVAL 1 DAY) FROM days WHERE d < :end
            )
            SELECT
              d.d                                   AS date,
              SUM(r.total_rooms)                    AS totalRoomsSum,
              SUM(COALESCE(pc.available_room, 0))   AS availableSum,
              MIN(pc.price)                         AS minPrice,
              MAX(pc.price)                         AS maxPrice
            FROM days d
            JOIN room r            ON 1=1
            JOIN accommodation a   ON a.accommodation_id = r.accommodation_id
            JOIN company c         ON c.company_id = a.company_id
            LEFT JOIN price_calendar pc
                   ON pc.room_id = r.room_id AND pc.date = d.d
            WHERE c.company_id = :companyId
              AND (:accommodationId IS NULL OR a.accommodation_id = :accommodationId)
            GROUP BY d.d
            ORDER BY d.d
            """, nativeQuery = true)
    List<DailyAggProjection> findDailyOverview(
            @Param("companyId") Long companyId,
            @Param("accommodationId") Long accommodationId,
            @Param("start") java.sql.Date start,
            @Param("end") java.sql.Date end
    );


    @Query(value = """
            WITH RECURSIVE days AS (
              SELECT :start AS d
              UNION ALL
              SELECT DATE_ADD(d, INTERVAL 1 DAY) FROM days WHERE d < :end
            )
            SELECT
              r.room_id                               AS roomId,
              r.name                                  AS roomName,
              d.d                                     AS date,
              COALESCE(pc.price, r.price)             AS price,
              COALESCE(pc.available_room, 0)          AS available,
              r.total_rooms                            AS totalRooms
            FROM days d
            JOIN room r            ON 1=1
            JOIN accommodation a   ON a.accommodation_id = r.accommodation_id
            JOIN company c         ON c.company_id = a.company_id
            LEFT JOIN price_calendar pc
                   ON pc.room_id = r.room_id AND pc.date = d.d
            WHERE c.company_id = :companyId
              AND (:accommodationId IS NULL OR a.accommodation_id = :accommodationId)
            ORDER BY r.room_id, d.d
            """, nativeQuery = true)
    List<GridCellProjection> findGridCells(
            @Param("companyId") Long companyId,
            @Param("accommodationId") Long accommodationId,
            @Param("start") java.sql.Date start,
            @Param("end") java.sql.Date end
    );
}
