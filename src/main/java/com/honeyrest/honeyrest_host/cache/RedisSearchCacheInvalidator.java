package com.honeyrest.honeyrest_host.cache;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 사용자 API 와 같은 Redis 의 {@link SearchCacheKeys#SEARCH_VERSION_KEY} 를 INCR 하고,
 * 숙소 상세 캐시 키({@link AccommodationCacheKeys#allFor})를 DEL 한다.
 * Redis 장애로 본 처리가 실패하면 안 되므로 예외는 삼키고 경고 로그만 남긴다.
 */
@Log4j2
public class RedisSearchCacheInvalidator implements SearchCacheInvalidator {

    private final StringRedisTemplate redisTemplate;

    public RedisSearchCacheInvalidator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void bumpNow() {
        try {
            Long version = redisTemplate.opsForValue().increment(SearchCacheKeys.SEARCH_VERSION_KEY);
            log.debug("검색 캐시 세대 증가: {}={}", SearchCacheKeys.SEARCH_VERSION_KEY, version);
        } catch (RuntimeException e) {
            log.warn("검색 캐시 세대 증가 실패(최대 TTL 동안 오래된 검색 결과가 보일 수 있음): {}", e.toString());
        }
    }

    @Override
    public void evictAccommodationNow(Long accommodationId) {
        if (accommodationId == null) return;
        try {
            Long deleted = redisTemplate.delete(AccommodationCacheKeys.allFor(accommodationId));
            log.debug("숙소 상세 캐시 삭제: accommodationId={}, deleted={}", accommodationId, deleted);
        } catch (RuntimeException e) {
            log.warn("숙소 상세 캐시 삭제 실패(TTL 동안 옛 상세 정보가 보일 수 있음): accommodationId={}, {}",
                    accommodationId, e.toString());
        }
    }
}
