package com.honeyrest.honeyrest_host.cache;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/** app.cache.redis.enabled 스위치에 따라 Redis/No-op 구현 중 하나만 등록되는지 확인한다 (Redis 서버 불필요). */
class SearchCacheConfigTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RedisAutoConfiguration.class))
            .withUserConfiguration(SearchCacheConfig.class);

    @Test
    void 값이_없으면_NoOp() {
        runner.run(ctx -> assertThat(ctx).getBean(SearchCacheInvalidator.class)
                .isInstanceOf(NoOpSearchCacheInvalidator.class));
    }

    @Test
    void false면_NoOp() {
        runner.withPropertyValues("app.cache.redis.enabled=false")
                .run(ctx -> assertThat(ctx).getBean(SearchCacheInvalidator.class)
                        .isInstanceOf(NoOpSearchCacheInvalidator.class));
    }

    @Test
    void true면_Redis_구현() {
        runner.withPropertyValues("app.cache.redis.enabled=true")
                .run(ctx -> {
                    assertThat(ctx).hasSingleBean(SearchCacheInvalidator.class);
                    assertThat(ctx).getBean(SearchCacheInvalidator.class)
                            .isInstanceOf(RedisSearchCacheInvalidator.class);
                });
    }
}
