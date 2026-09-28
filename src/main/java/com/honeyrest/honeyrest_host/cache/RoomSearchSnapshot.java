package com.honeyrest.honeyrest_host.cache;

import com.honeyrest.honeyrest_host.entity.Room;

import java.math.BigDecimal;

/**
 * 객실 중 사용자 검색 결과(남은 객실 수·노출 여부·가격·인원 필터)에 영향을 주는 필드만 모은 스냅샷.
 * 수정 전후 스냅샷이 다를 때만 검색 캐시 세대를 올려, 설명·이미지 같은 변경으로 캐시를 버리지 않게 한다.
 * <p>
 * JPA merge 는 영속 엔티티에 값을 덮어쓰므로 반드시 save 호출 <b>전에</b> {@link #of(Room)} 로 떠 둔다.
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
