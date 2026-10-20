package com.honeyrest.honeyrest_host.cache;

/** Redis 를 쓰지 않는 환경용. 사용자 API 캐시는 TTL 로만 갱신된다. */
public class NoOpSearchCacheInvalidator implements SearchCacheInvalidator {

    @Override
    public void bumpNow() {
    }

    @Override
    public void evictAccommodationNow(Long accommodationId) {
    }

    @Override
    public void evictAccommodationAfterCommit(Long accommodationId) {
        // 트랜잭션 동기화도 등록하지 않는다.
    }

    @Override
    public void bumpAfterCommit() {
        // 트랜잭션 동기화도 등록하지 않는다.
    }
}
