package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    // [기본 조회]
    // Pageable을 넘기면 페이징/정렬 모두 처리가능 (기본 findAll(Pageable)도 있음)

    // 상태 단일 필터 (사용자가 status 선택했을 때)
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
    @Query("select avg(r.rating) from Review r where r.accommodationId = :accId and r.status = 'VISIBLE'")
    Double findAverageRatingByAccommodation(@Param("accId") Long accommodationId);

    // [통계] 객실별 평균 평점
    @Query("select avg(r.rating) from Review r where r.roomId = :roomId and r.status = 'VISIBLE'")
    Double findAverageRatingByRoom(@Param("roomId") Long roomId);

    // [소프트 삭제] status만 바꾸는 업데이트 (하드 삭제를 피하고 싶을 때)
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Review r set r.status = 'HIDDEN' where r.reviewId = :id")
    int softHide(@Param("id") Long reviewId);

    // 리뷰 상태 변경
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update Review r set r.status = :status where r.reviewId = :id")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    long countByAccommodationIdAndStatus(Long accommodationId, String status);

    long countByRoomIdAndStatus(Long roomId, String status);

    Page<Review> findByAccommodationIdIn(List<Long> accommodationIds, Pageable pageable);

    Page<Review> findByAccommodationIdInAndStatus(List<Long> accommodationIds, String status, Pageable pageable);

}

