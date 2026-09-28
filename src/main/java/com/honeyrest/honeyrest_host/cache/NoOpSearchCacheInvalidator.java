package com.honeyrest.honeyrest_host.cache;

/**
 * Redis 를 쓰지 않는 환경(app.cache.redis.enabled=false: test/screenshot 프로필, 로컬 단독 실행)용.
 * 사용자 API 검색 캐시는 TTL 로만 갱신된다.
 */
public class NoOpSearchCacheInvalidator implements SearchCacheInvalidator {

    @Override
    public void bumpNow() {
        // 의도적으로 아무것도 하지 않는다.
    }

    @Override
    public void bumpAfterCommit() {
        // 트랜잭션 동기화도 등록하지 않는다.
    }
}
