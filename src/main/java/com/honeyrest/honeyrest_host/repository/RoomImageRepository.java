package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    List<RoomImage> findByRoomRoomIdOrderBySortOrderAsc(Long roomId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    void deleteByRoomRoomId(Long roomId);

    // 목록 최적화용: 여러 roomId에 대해 한 번에 0번 이미지만 가져오기(type이 엔티티에 없기 떄문에)
    @Query("select ri from RoomImage ri where ri.room.roomId in :roomIds and ri.sortOrder = 0")
    List<RoomImage> findMainImagesByRoomIds(@Param("roomIds") List<Long> roomIds);
}


