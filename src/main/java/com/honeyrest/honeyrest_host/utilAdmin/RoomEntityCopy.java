package com.honeyrest.honeyrest_host.utilAdmin;

import com.honeyrest.domain.entity.Room;

/**
 * 객실 수정용 빌더 도우미. 공유 도메인 {@link Room} 에 변경 메서드가 없어 빌더로 다시 만들어 merge 한다.
 * {@link #of(Room)} 는 모든 컬럼과 이미지 컬렉션 인스턴스를 채운 빌더를 돌려주므로 merge 뒤에도 이미지가 유지된다.
 */
public final class RoomEntityCopy {

    private RoomEntityCopy() {
    }

    /** 관리 중인 Room 의 현재 값(이미지 컬렉션 포함)으로 채운 빌더 */
    public static Room.RoomBuilder of(Room current) {
        return Room.builder()
                .roomId(current.getRoomId())
                .accommodation(current.getAccommodation())
                .name(current.getName())
                .type(current.getType())
                .price(current.getPrice())
                .maxOccupancy(current.getMaxOccupancy())
                .standardOccupancy(current.getStandardOccupancy())
                .extraPersonFee(current.getExtraPersonFee())
                .bedInfo(current.getBedInfo())
                .amenities(current.getAmenities())
                .description(current.getDescription())
                .totalRooms(current.getTotalRooms())
                .status(current.getStatus())
                .images(current.getImages());
    }
}
