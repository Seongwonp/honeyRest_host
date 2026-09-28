package com.honeyrest.honeyrest_host.support;

import com.honeyrest.honeyrest_host.entity.*;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * test 프로필(H2) 통합 테스트용 엔티티 픽스처.
 * 하드코딩된 ID 대신 매 테스트가 필요한 업체/숙소/객실/사용자를 직접 만든다.
 * 호출하는 테스트는 @Transactional 이어야 하며, 테스트 종료 시 롤백된다.
 */
public class JpaTestFixtures {

    private final EntityManager em;

    public JpaTestFixtures(EntityManager em) {
        this.em = em;
    }

    public Company company(String name) {
        Company c = Company.builder()
                .name(name)
                .businessNumber("000-00-" + shortId())
                .status("APPROVED")
                .build();
        em.persist(c);
        return c;
    }

    public Accommodation accommodation(Company company, String name) {
        AccommodationCategory category = AccommodationCategory.builder().name("호텔").build();
        em.persist(category);
        Region main = Region.builder().name("서울").level(1).build();
        em.persist(main);
        Region sub = Region.builder().name("강남구").level(2).parentId(main.getRegionId()).build();
        em.persist(sub);

        Accommodation a = Accommodation.builder()
                .company(company)
                .category(category)
                .mainRegion(main)
                .subRegion(sub)
                .name(name)
                .address("서울시 강남구 테스트로 1")
                .status("active")
                .build();
        em.persist(a);
        return a;
    }

    public Room room(Accommodation accommodation, String name, int totalRooms) {
        Room r = Room.builder()
                .accommodation(accommodation)
                .name(name)
                .price(BigDecimal.valueOf(100_000))
                .maxOccupancy(4)
                .standardOccupancy(2)
                .totalRooms(totalRooms)
                .status("active")
                .build();
        em.persist(r);
        return r;
    }

    public User user(String name) {
        User u = User.builder()
                .email(shortId() + "@test.honeyrest.com")
                .name(name)
                .role("GENERAL")
                .status("ACTIVE")
                .build();
        em.persist(u);
        return u;
    }

    public Reservation reservation(User user, Room room, String status, LocalDate checkIn, LocalDate checkOut) {
        Accommodation acc = room.getAccommodation();
        Reservation r = Reservation.builder()
                .user(user)
                .room(room)
                .accommodation(acc)
                .accommodationName(acc.getName())
                .roomName(room.getName())
                .reservationNumber("HR-TEST-" + shortId())
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .guestCount(2)
                .guestName("김짱구")
                .guestPhone("010-1234-5678")
                .price(BigDecimal.valueOf(100_000))
                .status(status)
                .build();
        em.persist(r);
        return r;
    }

    public void flushAndClear() {
        em.flush();
        em.clear();
    }

    private static String shortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
