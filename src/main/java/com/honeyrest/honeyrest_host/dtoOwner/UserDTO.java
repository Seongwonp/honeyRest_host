package com.honeyrest.honeyrest_host.dtoOwner;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long userId; //사용자 고유 식별자
    private String email; // 이메일 주소 (자체 가입용)
    private String socialType; // 소셜 로그인 타입 (KAKAO, GOOGLE)
    private String socialId; // 소셜 서비스 고유 ID
    private String name; // 사용자 이름
    private String phone; // 연락처
    private String profileImage; // 프로필 이미지 URL
    private LocalDate birthDate; // 생년월일
    private String gender; // 성별
    private Boolean marketingAgree; // 마케팅 정보 수신 동의 여부
    private int point; // 현재 포인트
    private String role; // 권한 (GENERAL, COMPANY_ADMIN, SUPER_ADMIN)
    private String status; // 계정 상태 (ACTIVE, SUSPENDED, DELETED)
    private boolean isVerified;
    private LocalDateTime lastLogin; // 마지막 로그인 시간
}
