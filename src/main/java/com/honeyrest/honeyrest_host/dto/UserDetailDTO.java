package com.honeyrest.honeyrest_host.dto;

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
public class UserDetailDTO {
    private Long userId;
    private String email;
    private String name;
    private String phone;
    private String profileImage;
    private LocalDate birthDate;
    private String gender;
    private Boolean marketingAgree;
    private Boolean isVerified;
    private int point;
    private String role; // 단일 역할
    private String status;
    private LocalDateTime lastLogin;

    private String socialType; // 소셜 로그인 타입 (KAKAO, GOOGLE)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 상세 용 확장들 탭별로 추가 가능
    // private List<PointTxDTO> pointHistory;
    // private List<ReservationSummaryDTO> reservations;
}
