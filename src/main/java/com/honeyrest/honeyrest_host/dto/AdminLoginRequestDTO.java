package com.honeyrest.honeyrest_host.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginRequestDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private String role; // 화면 표시용 (COMPANY_ADMIN, SUPER_ADMIN 등)
    private String name;
}
