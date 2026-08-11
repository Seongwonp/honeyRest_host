package com.honeyrest.honeyrest_host.security;

import com.honeyrest.honeyrest_host.config.JwtTokenProvider;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * P1-7 회귀 테스트: refresh token(typ=refresh)은 access token처럼 인증에 쓰일 수 없어야 한다.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock private UserRepository userRepository;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    private JwtTokenProvider jwtTokenProvider;
    private JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", "test-secret-key-must-be-at-least-32-chars-long!!");
        ReflectionTestUtils.setField(jwtTokenProvider, "expirationMs", 3_600_000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshExpirationMs", 1_209_600_000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "issuer", "honeyrest-admin");

        filter = new JwtAuthFilter(jwtTokenProvider, userRepository);
        SecurityContextHolder.clearContext();
    }

    @Test
    void refresh_token은_인증에_사용되지_않는다() throws Exception {
        String refreshToken = jwtTokenProvider.createRefreshToken(1L, "admin@test.local", "COMPANY_ADMIN");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + refreshToken);
        when(request.getRequestURI()).thenReturn("/admin/dashboard");

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void access_token은_정상적으로_인증된다() throws Exception {
        String accessToken = jwtTokenProvider.createAccessToken(1L, "admin@test.local", "COMPANY_ADMIN");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken);
        when(request.getRequestURI()).thenReturn("/admin/dashboard");

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("admin@test.local");
    }
}
