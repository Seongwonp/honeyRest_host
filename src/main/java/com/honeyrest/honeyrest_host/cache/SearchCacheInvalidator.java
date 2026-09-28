package com.honeyrest.honeyrest_host.cache;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 사용자 API 의 숙소 검색 캐시를 무효화(세대 번호 증가)한다.
 * <p>
 * 호스트가 검색 결과에 영향을 주는 데이터(예약 점유 상태, 객실 totalRooms/상태, 숙소 노출 상태,
 * 가격 캘린더, 숙소 승인)를 바꾸면 서비스 계층에서 {@link #bumpAfterCommit()} 을 호출한다.
 * <ul>
 *   <li>app.cache.redis.enabled=true : {@link RedisSearchCacheInvalidator} (Redis INCR)</li>
 *   <li>그 외(test/screenshot 프로필 등) : {@link NoOpSearchCacheInvalidator} (아무것도 하지 않음)</li>
 * </ul>
 */
public interface SearchCacheInvalidator {

    /** 세대 번호를 즉시 올린다. 구현체는 Redis 장애 등 예외를 호출자에게 던지지 않는다. */
    void bumpNow();

    /**
     * 트랜잭션 안이면 커밋이 끝난 뒤에, 밖이면 즉시 세대 번호를 올린다.
     * 커밋 전에 올리면 그 사이 사용자 API 가 아직 커밋되지 않은(옛) 재고로 새 세대 캐시를 채울 수 있고,
     * 롤백된 변경으로 캐시를 버리는 일도 생긴다. 한 트랜잭션에서 여러 번 불려도 커밋 후 한 번만 올린다.
     */
    default void bumpAfterCommit() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            bumpNow();
            return;
        }
        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            if (sync instanceof AfterCommitBump) {
                return; // 이미 이 트랜잭션에 등록됨
            }
        }
        TransactionSynchronizationManager.registerSynchronization(new AfterCommitBump(this));
    }

    /** 커밋 후 1회 bumpNow 를 호출하는 동기화 콜백 (중복 등록 판별용 타입). */
    record AfterCommitBump(SearchCacheInvalidator invalidator) implements TransactionSynchronization {
        @Override
        public void afterCommit() {
            invalidator.bumpNow();
        }
    }
}
