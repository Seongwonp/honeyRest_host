package com.honeyrest.honeyrest_host.cache;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 사용자 API 와 같은 Redis 의 {@link SearchCacheKeys#SEARCH_VERSION_KEY} 를 INCR 한다.
 * <p>
 * Redis 장애가 예약/객실/가격 변경 자체를 실패시키면 안 되므로 예외는 삼키고 경고 로그만 남긴다.
 * (이 경우 사용자 검색 결과는 최대 TTL 동안 옛 재고를 보여줄 수 있다.)
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
}
