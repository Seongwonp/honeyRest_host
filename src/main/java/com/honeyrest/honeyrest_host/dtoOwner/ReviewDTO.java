package com.honeyrest.honeyrest_host.dtoOwner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDTO {
    private Long reviewId;
    private Long reservationId;
    private Long userId;
    private Long accommodationId;     // 중복 저장 컬럼 (FK 아님)
    private Long roomId;
    private BigDecimal rating; // 종합 평점     // 평점 (0.00 ~ 5.00 등)
    private BigDecimal cleanlinessRating; // 청결도 평점
    private BigDecimal serviceRating; // 서비스 평점
    private BigDecimal facilitiesRating; // 시설 평점
    private BigDecimal locationRating; // 위치 평점
    private String content; // 리뷰 내용
    private String reply; // 업체/관리자 답변
    private Integer likeCount; // 좋아요 수
    private String status; // 상태(PUBLISHED, HIDDEN)
}
