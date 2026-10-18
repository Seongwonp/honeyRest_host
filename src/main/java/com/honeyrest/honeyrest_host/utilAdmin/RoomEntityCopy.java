package com.honeyrest.honeyrest_host.utilAdmin;

import com.honeyrest.domain.entity.Room;

/**
 * 객실 수정용 빌더 도우미.
 * <p>
 * 공유 도메인의 {@link Room} 에는 아직 세터나 변경용 도메인 메서드가 없어서, 호스트는 같은 roomId 의 Room 을
 * 빌더로 다시 만들어 save(merge) 하는 방식으로만 객실을 수정할 수 있다. 과거에는 서비스마다 빌더를 직접 채워
 * <ul>
 *   <li>빌더의 {@code images} 가 기본값(빈 리스트)이라 merge 뒤 영속 Room 의 이미지 컬렉션이 비고
 *       ({@code Room.images} 는 cascade=ALL — 나중에 orphanRemoval 이 켜지면 객실 수정마다 이미지가 전부 삭제된다),</li>
 *   <li>빠뜨린 필드는 null 로 덮어써질 위험</li>
 * </ul>
 * 이 있었다. {@link #of(Room)} 는 관리 중인 Room 의 모든 컬럼과 <b>그 이미지 컬렉션 자체</b>를 미리 채운 빌더를 돌려주므로
 * 호출부는 바꿀 필드만 덮어쓰면 된다. 이미지는 같은 컬렉션 인스턴스를 넘기기 때문에 merge 가 그대로 유지한다.
 * <p>
 * 도메인에 변경 메서드(예: Room.changeInfo/changeStatus)가 생기면 이 도우미 대신 관리 엔티티를 직접 변경하도록 바꾼다.
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
