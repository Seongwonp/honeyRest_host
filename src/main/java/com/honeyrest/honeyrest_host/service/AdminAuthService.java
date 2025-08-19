package com.honeyrest.honeyrest_host.service;


import com.honeyrest.honeyrest_host.config.JwtTokenProvider;
import com.honeyrest.honeyrest_host.dto.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dto.AdminSignupRequestDTO;
import com.honeyrest.honeyrest_host.dto.TokenResponseDTO;
import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.entity.enums.RoleType;
import com.honeyrest.honeyrest_host.entity.enums.UserStatus;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /** 관리자 가입 */
    public Long adminSignup(AdminSignupRequestDTO request) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        RoleType role = request.getRoleType() != null ? request.getRoleType() : RoleType.COMPANY_ADMIN;
        if (role == RoleType.GENERAL) {
            throw new IllegalArgumentException("관리자 가입은 GENERAL 권한을 허용하지 않습니다.");
        }

        User admin = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .gender(request.getGender() != null ? request.getGender() : "UNKNOWN")
                .marketingAgree(Boolean.TRUE.equals(request.getMarketingAgree()))
                .lastLogin(LocalDateTime.now())
                .point(0)
                .roleType(role) // COMPANY_ADMIN or SUPER_ADM
                .status(UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(admin);
        return saved.getUserId();
    }

    /** 로그인 (관리자 전용) */
    @Transactional(readOnly = true)
    public TokenResponseDTO login(AdminLoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }
        if (user.getRoleType() == null || user.getRoleType() == RoleType.GENERAL) {
            throw new IllegalStateException("관리자 권한이 없습니다.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail(), user.getRoleType());
        return TokenResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600_000L)
                .build();
    }
}