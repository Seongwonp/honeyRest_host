package com.honeyrest.honeyrest_host.cache;

import java.util.List;

/**
 * 사용자 API(honeyRest_user)의 숙소 단위 Redis 캐시 키 사본.
 * <p>
 * <b>동기화 주의:</b> 원본은 honeyRest_user {@code service/redis/AccommodationCacheKeys} 이다.
 * 사용자 API 가 키 형식을 바꾸거나 키를 추가하면 이 클래스도 같은 커밋 주기 안에 함께 고쳐야 한다.
 * 어긋나면 호스트에서 숙소/객실/이미지/환불 규정/태그를 바꿔도 사용자 상세 화면이 캐시 TTL 동안 옛 값을 보여 준다.
 * 키 문자열은 {@code AccommodationCacheKeysTest} 가 리터럴 그대로 검증한다.
 * (공유 도메인 모듈은 JPA 엔티티 전용이라 캐시 키를 두지 않았다.)
 */
public final class AccommodationCacheKeys {

    private AccommodationCacheKeys() {
    }

    public static String detail(Long id) {
        return "accommodation:detail:" + id;
    }

    public static String images(Long id) {
        return "accommodation:images:" + id;
    }

    public static String tags(Long id) {
        return "accommodation:tags:" + id;
    }

    public static String tagMap(Long id) {
        return "accommodation:tagmap:" + id;
    }

    public static String rating(Long id) {
        return "accommodation:rating:" + id;
    }

    public static String reviewCount(Long id) {
        return "reviewCount:accommodation:" + id;
    }

    public static String reviewList(Long id) {
        return "reviewList:accommodation:" + id;
    }

    public static String cancellationPolicy(Long id) {
        return "cancellationPolicy:accommodation:" + id;
    }

    /** 숙소 하나에 딸린 모든 캐시 키 (원본 allFor 와 같은 순서·구성). */
    public static List<String> allFor(Long id) {
        return List.of(detail(id), images(id), tags(id), tagMap(id), rating(id),
                reviewCount(id), reviewList(id), cancellationPolicy(id));
    }
}
