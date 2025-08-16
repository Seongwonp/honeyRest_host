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
}
