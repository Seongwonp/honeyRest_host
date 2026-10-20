package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.domain.entity.User;
import com.honeyrest.honeyrest_host.config.JwtTokenProvider;
import com.honeyrest.honeyrest_host.dtoAdmin.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 관리자 로그인 실패 응답: 없는 이메일과 틀린 비밀번호가 같은 IllegalArgumentException(→ 400)이어야 한다.
 */
@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;

    private AdminAuthService service;

    @BeforeEach
    void setUp() {
        service = new AdminAuthService(userRepository, passwordEncoder, jwtTokenProvider);
    }

    private static AdminLoginRequestDTO login(String email, String pw) {
        AdminLoginRequestDTO dto = new AdminLoginRequestDTO();
        dto.setEmail(email);
        dto.setPassword(pw);
        return dto;
    }

    @Test
    void 없는_이메일은_틀린_비밀번호와_같은_400_메시지() {
        when(userRepository.findByEmail("nobody@test.com")).thenReturn(null);

        assertThatThrownBy(() -> service.login(login("Nobody@test.com ", "pw")))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage(AdminAuthService.BAD_CREDENTIALS);
    }

    @Test
    void 틀린_비밀번호도_같은_메시지() {
        User admin = User.builder().userId(1L).email("admin@test.com").passwordHash("hash")
                .role("COMPANY_ADMIN").status("ACTIVE").build();
        when(userRepository.findByEmail("admin@test.com")).thenReturn(admin);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> service.login(login("admin@test.com", "wrong")))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage(AdminAuthService.BAD_CREDENTIALS);
    }
}
