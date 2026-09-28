package com.honeyrest.honeyrest_host.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 검색 캐시 무효화 빈 선택.
 * <p>
 * app.cache.redis.enabled=true 일 때만 Redis(Lettuce) 구현을 등록한다. false 이거나 값이 없으면
 * No-op 구현을 등록하므로 test/screenshot 프로필은 Redis 없이 뜬다.
 * (Lettuce 연결 팩토리는 자동 설정으로 만들어지지만 실제 명령을 보내기 전까지 접속하지 않는다.)
 */
@Configuration
public class SearchCacheConfig {

    @Bean
    @ConditionalOnProperty(name = "app.cache.redis.enabled", havingValue = "true")
    public SearchCacheInvalidator redisSearchCacheInvalidator(StringRedisTemplate stringRedisTemplate) {
        return new RedisSearchCacheInvalidator(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "app.cache.redis.enabled", havingValue = "false", matchIfMissing = true)
    public SearchCacheInvalidator noOpSearchCacheInvalidator() {
        return new NoOpSearchCacheInvalidator();
    }
}
