package com.honeyrest.honeyrest_host.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "point_history")
public class PointHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id",nullable = false)
    private Long pointHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer amount; // 변동 포인트

    @Column(nullable = false)
    private String type;

    @Column(length = 255)
    private String reason; // 변동 사유

    @Column(name = "related_id")
    private Long relatedId; // 관련 예약/리뷰 ID (NULL 가능)

    @Column(nullable = false)
    private Integer balance; // 변동 후 잔액

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // 적립 포인트 만료일 (null 가능)

}
