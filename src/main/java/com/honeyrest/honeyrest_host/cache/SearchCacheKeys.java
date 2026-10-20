package com.honeyrest.honeyrest_host.cache;

/**
 * 사용자 API 와 공유하는 Redis 검색 캐시 키.
 * 원본 honeyRest_user {@code SearchCacheVersionService.VERSION_KEY} 와 함께 바꿀 것.
 */
public final class SearchCacheKeys {

    /** 숙소 검색 결과 캐시의 세대 번호 키 (INCR 대상). */
    public static final String SEARCH_VERSION_KEY = "search:recommend:version";

    private SearchCacheKeys() {
    }
}
