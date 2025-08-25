package com.honeyrest.honeyrest_host.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 50, nullable = false)
    private String type; // 알림 유형 (RESERVATION_CONFIRM, REVIEW_REPLY, EVENT 등)

    @Column(length = 50, nullable = false)
    private String title; // 알림 제목

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "target_url",length = 50)
    private String targetUrl; // 알림 클릭 시 이동할 URL

    @Column(name = "is_read",nullable = false)
    private boolean isRead = false; // 읽음 여부(기본값 false)

}
