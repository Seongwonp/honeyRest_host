package com.honeyrest.honeyrest_host.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; //사용자 고유 식별자

    @Column(nullable = false, length = 100, unique = true)
    private String email; // 이메일 주소 (자체 가입용)

    @Column(name = "password_hash", length = 255)
    private String passwordHash; // 비밀번호 해시 (자체 가입용)

    @Column(name = "social_type", length = 20)
    private String socialType; // 소셜 로그인 타입 (KAKAO, GOOGLE)

    @Column(name = "social_id", length = 100)
    private String socialId; // 소셜 서비스 고유 ID

    @Column(nullable = false, length = 50)
    private String name; // 사용자 이름

    @Column(length = 20)
    private String phone; // 연락처

    @Column(name = "profile_image", length = 500)
    private String profileImage; // 프로필 이미지 URL

    @Column(name = "birth_date")
    private LocalDate birthDate; // 생년월일

    @Column(length = 10)
    private String gender; // 성별

    @Column(name = "marketing_agree")
    private Boolean marketingAgree; // 마케팅 정보 수신 동의 여부

    private int point; // 현재 포인트

    @Column(length = 20)
    private String role; // 권한 (GENERAL, COMPANY_ADMIN, SUPER_ADMIN)

    @Column(length = 20)
    private String status; // 계정 상태 (ACTIVE, SUSPENDED, DELETED)

    @Column(name = "last_login")
    private LocalDateTime lastLogin; // 마지막 로그인 시간
}
