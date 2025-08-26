package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // 페이징
    Page<Room> findByAccommodation_AccommodationId(Long accommodationId, Pageable pageable);

    void deleteByAccommodation_AccommodationId(Long accommodationId);

    // 재고 차감: 재고가 0 초과일 때만 1 감소. 성공 시 1 반환, 실패 시 0
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Room r set r.totalRooms = r.totalRooms - 1 where r.roomId = :roomId and r.totalRooms > 0")
    int decreaseStock(@Param("roomId") Long roomId);

    // 재고 복구: 무조건 1 증가 (취소 시)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Room r set r.totalRooms = r.totalRooms + 1 where r.roomId = :roomId")
    int increaseStock(@Param("roomId") Long roomId);

    // companyId는 accommodation → company 로 타고 감 (엔티티 매핑 기준)
    @Query("""
        select r from Room r
        join r.accommodation a
        join a.company c
        where c.companyId = :companyId
          and (:accommodationId is null or a.accommodationId = :accommodationId)
    """)
    Page<Room> findRoomsOfCompany(@Param("companyId") Long companyId,
                                  @Param("accommodationId") Long accommodationId, Pageable pageable);
    @Query("""
    select r from Room r
    join r.accommodation a
    join a.company c
    where c.companyId = :companyId
      and (:accommodationId is null or a.accommodationId = :accommodationId)
""")
    List<Room> findRoomsOfCompany(@Param("companyId") Long companyId,
                                  @Param("accommodationId") Long accommodationId);
}
