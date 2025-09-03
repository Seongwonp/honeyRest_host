package com.honeyrest.honeyrest_host.repositoryAdmin;


import com.honeyrest.honeyrest_host.dto.ReviewImageDTO;
import com.honeyrest.honeyrest_host.entity.ReviewImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


public interface ReviewImageRepository extends CrudRepository<ReviewImage, Long> {

    // 1) 상세 페이지/목록에서 한 리뷰의 모든 이미지 (엔티티로)
    List<ReviewImage> findAllByReview_ReviewIdOrderByImageIdAsc(Long reviewId);

    /* 특정 리뷰 이미지 개수 카운트 뱃지/조건 */
    int countByReview_ReviewId(Long reviewId);

    /* 여러 리뷰에 속한 이미지 모두 가져오기 (관리자 목록에서 필요할 수도 있음) */
    List<ReviewImage> findAllByReview_ReviewIdIn(Collection<Long> reviewIds);

    // dto 투영
    @Query("""
            select new com.honeyrest.honeyrest_host.dto.ReviewImageDTO(i.imageId, i.review.reviewId, i.imageUrl)
            from ReviewImage i
            where i.review.reviewId = :reviewId
            order by i.imageId asc
            """)
    List<ReviewImageDTO> findDtosByReviewId(@Param("reviewId") Long reviewId);

    @Query("""
            select new com.honeyrest.honeyrest_host.dto.ReviewImageDTO(i.imageId, i.review.reviewId, i.imageUrl)
            from ReviewImage i
            where i.review.reviewId in :reviewIds
            order by i.review.reviewId asc, i.imageId asc
            """)
    List<ReviewImageDTO> findDtosByReviewIdIn(@Param("reviewIds") Collection<Long> reviewIds);


    // DTO 투영 버전
    @Query("""
            select new com.honeyrest.honeyrest_host.dto.ReviewImageDTO(i.imageId, i.review.reviewId, i.imageUrl)
            from ReviewImage i
            where i.review.reviewId = :reviewId
            order by i.imageId asc
            """)
    Page<ReviewImageDTO> findFirstDtoByReviewId(@Param("reviewId") Long reviewId, Pageable pageable);

    // 이미지 n장 표시 쉬워지게 하기 위함.
    @Query("""
            select i.review.reviewId as reviewId, count(i) as cnt
            from ReviewImage i
            where i.review.reviewId in :reviewIds
            group by i.review.reviewId
            """)
    List<Object[]> countGroupedByReviewId(@Param("reviewIds") Collection<Long> reviewIds);

    // 존재 여부/ 삭제
    boolean existsByReview_ReviewId(Long reviewId);

    // 리뷰 삭제시 이미지 정리용
    @Transactional
    long deleteByReview_ReviewId(Long reviewId); // 리뷰 삭제 시 이미지 정리용
}
