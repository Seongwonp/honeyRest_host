package com.honeyrest.honeyrest_host.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * app.cache.redis.enabled=true 일 때만 Redis 구현을, 그 외에는 No-op 구현을 등록한다.
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
