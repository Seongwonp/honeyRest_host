package com.honeyrest.honeyrest_host.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchCacheInvalidatorTest {

    @Mock StringRedisTemplate redisTemplate;
    @Mock ValueOperations<String, String> valueOps;

    private RedisSearchCacheInvalidator invalidator;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        invalidator = new RedisSearchCacheInvalidator(redisTemplate);
    }

    @AfterEach
    void clearSync() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    /**
     * 사용자 API(honeyRest_user) SearchCacheVersionService.VERSION_KEY 와 같은 문자열이어야 한다.
     * 이 테스트가 깨졌다면 두 저장소의 키를 함께 바꿨는지 확인한다.
     */
    @Test
    void 키_문자열은_사용자_API와_같다() {
        assertThat(SearchCacheKeys.SEARCH_VERSION_KEY).isEqualTo("search:recommend:version");
    }

    @Test
    void 트랜잭션_밖이면_즉시_같은_키를_INCR_한다() {
        invalidator.bumpAfterCommit();

        verify(valueOps).increment("search:recommend:version");
    }

    @Test
    void 트랜잭션_안이면_커밋_후에만_한번_INCR_한다() {
        TransactionSynchronizationManager.initSynchronization();

        invalidator.bumpAfterCommit();
        invalidator.bumpAfterCommit(); // 같은 트랜잭션에서 여러 번 불려도

        verify(valueOps, never()).increment("search:recommend:version");
        assertThat(TransactionSynchronizationManager.getSynchronizations()).hasSize(1);

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(valueOps, times(1)).increment("search:recommend:version");
    }

    @Test
    void 롤백되면_INCR_하지_않는다() {
        TransactionSynchronizationManager.initSynchronization();

        invalidator.bumpAfterCommit();
        TransactionSynchronizationManager.getSynchronizations()
                .forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));

        verify(valueOps, never()).increment("search:recommend:version");
    }

    @Test
    void Redis_장애는_호출자에게_전파하지_않는다() {
        when(valueOps.increment("search:recommend:version"))
                .thenThrow(new RedisConnectionFailureException("down"));

        assertThatCode(invalidator::bumpNow).doesNotThrowAnyException();
    }

    @Test
    void NoOp_구현은_동기화도_등록하지_않는다() {
        TransactionSynchronizationManager.initSynchronization();

        new NoOpSearchCacheInvalidator().bumpAfterCommit();

        assertThat(TransactionSynchronizationManager.getSynchronizations()).isEmpty();
    }
}
