package com.honeyrest.honeyrest_host.dto;

import com.honeyrest.honeyrest_host.entity.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminSignupRequestDTO {
    @NotBlank
    private String email;
    @NotBlank private String password;
    @NotBlank private String name;
    // 선택 입력: 미입력시 COMPANY_ADMIN 기본 부여
    private RoleType roleType; // COMPANY_ADMIN | SUPER_ADMIN
}
