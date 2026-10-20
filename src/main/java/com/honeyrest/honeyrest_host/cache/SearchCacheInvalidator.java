package com.honeyrest.honeyrest_host.cache;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 사용자 API 의 Redis 캐시를 무효화한다.
 * 검색 결과에 영향을 주는 변경은 {@link #bumpAfterCommit()}(세대 번호 증가),
 * 상세 화면에 보이는 변경은 {@link #evictAccommodationAfterCommit(Long)}(키 삭제)을 호출한다.
 */
public interface SearchCacheInvalidator {

    /** 세대 번호를 즉시 올린다. 구현체는 Redis 장애 등 예외를 호출자에게 던지지 않는다. */
    void bumpNow();

    /** 숙소 상세 캐시 키들을 즉시 지운다. 구현체는 예외를 호출자에게 던지지 않는다. */
    void evictAccommodationNow(Long accommodationId);

    /**
     * 트랜잭션 안이면 커밋 후에, 밖이면 즉시 세대 번호를 올린다(커밋 전에 올리면 옛 재고로 새 캐시가 채워질 수 있다).
     * 한 트랜잭션에서 여러 번 불려도 한 번만 올린다.
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

    /**
     * 트랜잭션 안이면 커밋 후에, 밖이면 즉시 숙소 상세 캐시를 지운다 (커밋 전 삭제는 옛 값 재적재 위험이 있다).
     * 한 트랜잭션에서 여러 숙소/여러 번 불려도 커밋 후 숙소별로 한 번만 지운다.
     */
    default void evictAccommodationAfterCommit(Long accommodationId) {
        if (accommodationId == null) return;
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            evictAccommodationNow(accommodationId);
            return;
        }
        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            if (sync instanceof AfterCommitEvict evict) {
                evict.accommodationIds().add(accommodationId);
                return;
            }
        }
        AfterCommitEvict evict = new AfterCommitEvict(this, new LinkedHashSet<>());
        evict.accommodationIds().add(accommodationId);
        TransactionSynchronizationManager.registerSynchronization(evict);
    }

    /** 커밋 후 1회 bumpNow 를 호출하는 동기화 콜백 (중복 등록 판별용 타입). */
    record AfterCommitBump(SearchCacheInvalidator invalidator) implements TransactionSynchronization {
        @Override
        public void afterCommit() {
            invalidator.bumpNow();
        }
    }

    /** 커밋 후 모아 둔 숙소들의 상세 캐시를 지우는 동기화 콜백. */
    record AfterCommitEvict(SearchCacheInvalidator invalidator, Set<Long> accommodationIds)
            implements TransactionSynchronization {
        @Override
        public void afterCommit() {
            accommodationIds.forEach(invalidator::evictAccommodationNow);
        }
    }
}
