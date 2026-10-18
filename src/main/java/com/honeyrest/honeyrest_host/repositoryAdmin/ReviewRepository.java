package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    // [기본 조회]
    // Pageable을 넘기면 페이징/정렬 모두 처리가능 (기본 findAll(Pageable)도 있음)

    // 상태 필터. V12 에서 과거 노출 값 VISIBLE 을 PUBLISHED 로 정규화했으므로 단일 값(=)으로 조회한다
    // (저장 값: com.honeyrest.domain.type.ReviewStatus.ALL = PUBLISHED/HIDDEN/DELETED)
    Page<Review> findByStatus(String status, Pageable pageable);

    // [조건 조회 1] 숙소 기준 + 상태 필터
    Page<Review> findByAccommodationIdAndStatus(Long accommodationId, String status, Pageable pageable);

    // [조건 조회 2] 객실 기준 + 상태 필터
    Page<Review> findByRoomIdAndStatus(Long roomId, String status, Pageable pageable);

    // [조건 조회 3] 숙소 기준(상태 무관)
    Page<Review> findByAccommodationId(Long accommodationId, Pageable pageable);

    // [조건 조회 4] 객실 기준(상태 무관)
    Page<Review> findByRoomId(Long roomId, Pageable pageable);

    // [통계] 숙소별 평균 평점
    @Query("select avg(r.rating) from Review r where r.accommodationId = :accId and r.status = 'PUBLISHED'")
    Double findAverageRatingByAccommodation(@Param("accId") Long accommodationId);

    // [통계] 객실별 평균 평점
    @Query("select avg(r.rating) from Review r where r.roomId = :roomId and r.status = 'PUBLISHED'")
    Double findAverageRatingByRoom(@Param("roomId") Long roomId);

    long countByAccommodationIdAndStatus(Long accommodationId, String status);

    long countByRoomIdAndStatus(Long roomId, String status);

    Page<Review> findByAccommodationIdIn(List<Long> accommodationIds, Pageable pageable);

    Page<Review> findByAccommodationIdInAndStatus(List<Long> accommodationIds, String status, Pageable pageable);

}

