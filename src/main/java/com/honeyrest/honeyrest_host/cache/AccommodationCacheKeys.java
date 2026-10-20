package com.honeyrest.honeyrest_host.cache;

import java.util.List;

/**
 * 사용자 API 숙소 상세 Redis 캐시 키 사본.
 * 원본 honeyRest_user {@code service/redis/AccommodationCacheKeys} 와 함께 바꿀 것 (AccommodationCacheKeysTest 가 검증).
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
