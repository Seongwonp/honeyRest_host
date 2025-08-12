package com.honeyrest.honeyrest_host.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 중복 저장 컬럼 (FK 아님)
    @Column(name = "accommodation_id", nullable = false)
    private Long accommodationId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    // 평점 (0.00 ~ 5.00 등)
    @Column(name = "rating", precision = 3, scale = 2, nullable = false)
    private BigDecimal rating; // 종합 평점

    @Column(name = "cleanliness_rating", precision = 3, scale = 2)
    private BigDecimal cleanlinessRating; // 청결도 평점

    @Column(name = "service_rating", precision = 3, scale = 2)
    private BigDecimal serviceRating; // 서비스 평점

    @Column(name = "facilities_rating", precision = 3, scale = 2)
    private BigDecimal facilitiesRating; // 시설 평점

    @Column(name = "location_rating", precision = 3, scale = 2)
    private BigDecimal locationRating; // 위치 평점

    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // 리뷰 내용

    @Column(name = "reply", columnDefinition = "TEXT")
    private String reply; // 업체/관리자 답변

    @Column(name = "like_count")
    private Integer likeCount; // 좋아요 수

    @Column(name = "status", length = 20, nullable = false)
    private String status; // 상태(PUBLISHED, HIDDEN)
}
