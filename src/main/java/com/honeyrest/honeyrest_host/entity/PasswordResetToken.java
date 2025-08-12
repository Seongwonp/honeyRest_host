package com.honeyrest.honeyrest_host.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true, nullable = false)
    private String tokenValue;

    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;

    private Boolean isUsed;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.isUsed = false;
    }
}