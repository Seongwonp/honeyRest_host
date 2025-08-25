package com.honeyrest.honeyrest_host.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long reviewId;

    @NotNull
    private Long reservationId;

    @NotNull
    private Long userId;

    @NotNull
    private Long accommodationId; // 중복 저장 컬럼 (FK 아님)

    @NotNull
    private Long roomId;

    @NotNull
    private BigDecimal rating; // 종합 평점 (0.00 ~ 5.00 등)


    @DecimalMin(value = "0.00")
    @DecimalMax(value = "5.00")
    private BigDecimal cleanlinessRating; // 청결도 평점

    @DecimalMin(value = "0.00")
    @DecimalMax(value = "5.00")
    private BigDecimal serviceRating; // 서비스 평점

    @DecimalMin(value = "0.00")
    @DecimalMax(value = "5.00")
    private BigDecimal facilitiesRating; // 시설 평점

    @DecimalMin(value = "0.00")
    @DecimalMax(value = "5.00")
    private BigDecimal locationRating; // 위치 평점

    private String content; // 리뷰 내용
    private String reply; // 업체/관리자 답변

    private Integer likeCount; // 좋아요 수

    @NotBlank
    private String status; //VISIBLE 노출, HIDDEN 숨김(소프트 삭제),REPORTED 신고,PENDING 임시저장/검토 전
}


