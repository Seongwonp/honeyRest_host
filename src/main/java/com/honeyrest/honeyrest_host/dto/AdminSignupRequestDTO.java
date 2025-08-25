package com.honeyrest.honeyrest_host.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminSignupRequestDTO {
    @NotBlank
    private String email;

    @NotBlank private String password;
    @NotBlank private String name;
    private String phone;

    // yyyy-MM-dd 로 받기 (예: 1995-08-16)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    // 선택 입력: 미입력시 COMPANY_ADMIN 기본 부여
    private String role; // COMPANY_ADMIN | SUPER_ADMIN

    private int point; // 사용자에 대한 포인트

    private Boolean isVerified = false; // 이메일 인증 여부

    private String gender;          // 성별 (예: "M", "F")
    private Boolean marketingAgree; // 마케팅 수신 동의


}
