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

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void singUp(AdminSignupRequestDTO request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new IllegalArgumentException("이미 사용중인 이메일 입니다.");
        });

        RoleType finalRole = (request.getRoleType() == null ? RoleType.COMPANY_ADMIN : request.getRoleType());
        if (finalRole == RoleType.GENERAL) {
            throw new IllegalArgumentException("관리자 가입은 일반 회원 권한을 허용하지 않습니다.");
        }

        User admin = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .roleType(finalRole)
                .status(UserStatus.ACTIVE)
                .build();

        // [선택] 정말 외부에서 만든 생성/수정일을 강제로 넣고 싶다면(권장X)
    /*
    if (request.getCreatedAt() != null || request.getUpdatedAt() != null) {
        setField(admin, "createdAt", request.getCreatedAt());
        setField(admin, "updatedAt", request.getUpdatedAt());
    }
    */

        userRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public TokenResponseDTO login(AdminLoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }
        if (user.getRoleType() == RoleType.GENERAL) {
            throw new IllegalStateException("관리자 권한이 없습니다.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail(), user.getRoleType());
        return TokenResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600_000L) // jwt.expiration-ms와 일치
                .build();
    }
}
