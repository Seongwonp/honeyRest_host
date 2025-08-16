package com.honeyrest.honeyrest_host.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeyrest.honeyrest_host.entity.enums.RoleType;
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
    private RoleType roleType; // COMPANY_ADMIN | SUPER_ADMIN

    /* [선택] 정말 외부에서 만든 생성일/수정일을 저장해야 한다면 아래 필드 추가
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    */
}
