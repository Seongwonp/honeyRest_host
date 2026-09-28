package com.honeyrest.honeyrest_host.cache;

/**
 * 사용자 API(honeyRest_user)와 공유하는 Redis 검색 캐시 키.
 * <p>
 * <b>양쪽 저장소가 반드시 같은 문자열을 써야 한다.</b>
 * 원본: honeyRest_user {@code service/redis/SearchCacheVersionService.VERSION_KEY}.
 * 사용자 API 는 검색 결과 캐시 키(search:recommend:*)에 이 세대 번호를 넣고, 세대가 오르면 이전 세대 키를
 * 모두 버린다(남은 키는 TTL 로 자연 소멸). 한쪽에서 키 이름을 바꾸면 호스트의 재고·가격·노출 변경이
 * 검색 결과에 최대 TTL(6시간) 동안 반영되지 않으므로, 이름을 바꿀 때는 두 저장소를 함께 수정한다.
 * (SearchCacheKeysTest 가 문자열 그대로를 검증한다.)
 */
public final class SearchCacheKeys {

    /** 숙소 검색 결과 캐시의 세대 번호 키 (INCR 대상). */
    public static final String SEARCH_VERSION_KEY = "search:recommend:version";

    private SearchCacheKeys() {
    }
}
