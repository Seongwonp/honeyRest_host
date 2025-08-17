package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Review;
import com.honeyrest.honeyrest_host.entity.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    // [기본 조회]
    // Pageable을 넘기면 페이징/정렬 모두 처리가능 (기본 findAll(Pageable)도 있음)

    // [조건 조회 1] 숙소 기준 + 상태 필터
    Page<Review> findByAccommodationIdAndStatus(Long accommodationId, ReviewStatus status, Pageable pageable);

    // [조건 조회 2] 객실 기준 + 상태 필터
    Page<Review> findByRoomIdAndStatus(Long roomId, ReviewStatus status, Pageable pageable);

    // [조건 조회 3] 숙소 기준(상태 무관)
    Page<Review> findByAccommodationId(Long accommodationId, Pageable pageable);

    // [조건 조회 4] 객실 기준(상태 무관)
    Page<Review> findByRoomId(Long roomId, Pageable pageable);

    // [통계] 숙소별 평균 평점
    @Query("select avg(r.rating) from Review r where r.accommodationId = :accId and r.status = 'VISIBLE'")
    Double findAverageRatingByAccommodation(@Param("accId") Long accommodationId);

    // [통계] 객실별 평균 평점
    @Query("select avg(r.rating) from Review r where r.roomId = :roomId and r.status = 'VISIBLE'")
    Double findAverageRatingByRoom(@Param("roomId") Long roomId);

    // [소프트 삭제] status만 바꾸는 업데이트 (하드 삭제를 피하고 싶을 때)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Review r set r.status = 'HIDDEN' where r.reviewId = :id")
    int softHide(@Param("id") Long reviewId);
}

