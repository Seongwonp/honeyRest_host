package com.honeyrest.honeyrest_host.cache;

import com.honeyrest.domain.entity.Room;

import java.math.BigDecimal;

/**
 * 사용자 검색 결과에 영향을 주는 객실 필드 스냅샷. 전후가 다를 때만 검색 캐시를 무효화한다.
 * merge 가 영속 엔티티를 덮어쓰므로 save 전에 {@link #of(Room)} 로 만든다.
 */
public record RoomSearchSnapshot(Long accommodationId, Integer totalRooms, String status,
                                 BigDecimal price, Integer maxOccupancy) {

    public static RoomSearchSnapshot of(Room room) {
        if (room == null) return null;
        Long accId = room.getAccommodation() != null ? room.getAccommodation().getAccommodationId() : null;
        return new RoomSearchSnapshot(accId, room.getTotalRooms(), room.getStatus(),
                room.getPrice(), room.getMaxOccupancy());
    }

    /** BigDecimal 은 scale 차이(100 vs 100.00)를 같은 값으로 본다. */
    public boolean differsFrom(RoomSearchSnapshot other) {
        if (other == null) return true;
        return !java.util.Objects.equals(accommodationId, other.accommodationId)
                || !java.util.Objects.equals(totalRooms, other.totalRooms)
                || !java.util.Objects.equals(status, other.status)
                || !java.util.Objects.equals(maxOccupancy, other.maxOccupancy)
                || compare(price, other.price) != 0;
    }

    private static int compare(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) return (a == b) ? 0 : 1;
        return a.compareTo(b);
    }
}
