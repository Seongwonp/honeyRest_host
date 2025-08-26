package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.AdminSignupRequestDTO;
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
@RequestMapping("/api/owner/auth")
@RequiredArgsConstructor
public class OwnerAuthController {
    private final AdminAuthService adminAuthService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody AdminSignupRequestDTO adminSignupRequestDTO) {
        adminAuthService.adminSignup(adminSignupRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}