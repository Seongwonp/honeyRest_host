package com.honeyrest.honeyrest_host.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyCollection;

/**
 * 사용자 API(honeyRest_user) service/redis/AccommodationCacheKeys 와 같은 문자열인지 리터럴로 검증한다.
 * 이 테스트가 깨졌다면 두 저장소의 키를 함께 바꿨는지 확인한다.
 */
class AccommodationCacheKeysTest {

    @AfterEach
    void clearSync() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void 키_문자열은_사용자_API와_같다() {
        assertThat(AccommodationCacheKeys.allFor(7L)).containsExactly(
                "accommodation:detail:7",
                "accommodation:images:7",
                "accommodation:tags:7",
                "accommodation:tagmap:7",
                "accommodation:rating:7",
                "reviewCount:accommodation:7",
                "reviewList:accommodation:7",
                "cancellationPolicy:accommodation:7");
    }

    @Test
    void 트랜잭션_안이면_커밋_후_숙소별로_한번만_지운다() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        RedisSearchCacheInvalidator invalidator = new RedisSearchCacheInvalidator(redis);
        TransactionSynchronizationManager.initSynchronization();

        invalidator.evictAccommodationAfterCommit(7L);
        invalidator.evictAccommodationAfterCommit(7L);
        invalidator.evictAccommodationAfterCommit(8L);

        verify(redis, never()).delete(anyCollection());
        assertThat(TransactionSynchronizationManager.getSynchronizations()).hasSize(1);

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(redis, times(1)).delete(AccommodationCacheKeys.allFor(7L));
        verify(redis, times(1)).delete(AccommodationCacheKeys.allFor(8L));
    }

    @Test
    void 롤백되면_지우지_않고_트랜잭션_밖이면_즉시_지운다() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        RedisSearchCacheInvalidator invalidator = new RedisSearchCacheInvalidator(redis);

        TransactionSynchronizationManager.initSynchronization();
        invalidator.evictAccommodationAfterCommit(3L);
        TransactionSynchronizationManager.getSynchronizations()
                .forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));
        verify(redis, never()).delete(anyCollection());
        TransactionSynchronizationManager.clearSynchronization();

        invalidator.evictAccommodationAfterCommit(3L);
        verify(redis).delete(List.of(
                "accommodation:detail:3", "accommodation:images:3", "accommodation:tags:3",
                "accommodation:tagmap:3", "accommodation:rating:3", "reviewCount:accommodation:3",
                "reviewList:accommodation:3", "cancellationPolicy:accommodation:3"));
    }
}
