package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dto.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dto.AdminSignupRequestDTO;
import com.honeyrest.honeyrest_host.dto.TokenResponseDTO;
import com.honeyrest.honeyrest_host.service.AdminAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AdminAuthService adminAuthService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody AdminSignupRequestDTO adminSignupRequestDTO) {
        adminAuthService.singUp(adminSignupRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public TokenResponseDTO login(@Valid @RequestBody AdminLoginRequestDTO adminLoginRequestDTO) {
        return adminAuthService.login(adminLoginRequestDTO);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
