package com.honeyrest.honeyrest_host.service;


import com.honeyrest.honeyrest_host.config.JwtTokenProvider;
import com.honeyrest.honeyrest_host.dto.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dto.AdminSignupRequestDTO;
import com.honeyrest.honeyrest_host.dto.TokenResponseDTO;
import com.honeyrest.honeyrest_host.entity.User;
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
//        if (userRepository.findByEmail(request.getEmail())) {
//            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
//        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        String role = request.getRole() != null ? request.getRole() : "COMPANY_ADMIN";
        if (role == "GENERAL") {
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
                .isVerified(true)
                .lastLogin(LocalDateTime.now())
                .point(0)
                .role("COMPANY_ADMIN") // COMPANY_ADMIN or SUPER_ADM
                .status("ACTIVE")
                .build();

        User saved = userRepository.save(admin);
        return saved.getUserId();
    }

    /** 로그인 (관리자 전용) */
    @Transactional(readOnly = true)
    public TokenResponseDTO login(AdminLoginRequestDTO request) {
        // 이메일 정규화(권장)
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email);

        //  status: String 비교
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }

        // role: String 비교 (GENERAL이면 관리자 아님)
        String roleType = user.getRole(); // String으로 가정
        if (roleType == null || "GENERAL".equalsIgnoreCase(roleType)) {
            throw new IllegalStateException("관리자 권한이 없습니다.");
        }

        // 소셜 계정 대비: passwordHash null일 수 있으면 방어
        if (user.getPasswordHash() == null ||
            !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // ✅ 토큰 생성도 문자열 roleType 사용(메서드 시그니처를 문자열 받도록 수정/오버로드)
        String token = jwtTokenProvider.createAccessToken(
                user.getUserId(),
                user.getEmail(),
                roleType.toUpperCase()   // 표준화
        );

        return TokenResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3_600_000L)
                .build();
    }
}