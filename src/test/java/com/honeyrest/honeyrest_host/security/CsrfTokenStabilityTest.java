package com.honeyrest.honeyrest_host.security;

import com.honeyrest.honeyrest_host.config.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 회귀 테스트: JWT 로 인증된 일반 요청마다 CSRF 토큰이 교체되면 안 된다.
 * (교체된 쿠키가 큰 화면 응답에서 누락되어 브라우저의 폼 POST 가 403 이 되던 문제)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CsrfTokenStabilityTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void JWT_인증된_GET_요청은_기존_CSRF_쿠키를_교체하지_않는다() {
        String token = jwtTokenProvider.createAccessToken(1L, "company-admin@test.local", "COMPANY_ADMIN");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.add(HttpHeaders.COOKIE, "XSRF-TOKEN=existing-token-value");

        ResponseEntity<String> response = restTemplate.exchange(
                "/admin/__csrf-stability-probe", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        List<String> setCookies = response.getHeaders().getOrDefault(HttpHeaders.SET_COOKIE, List.of());
        assertThat(setCookies)
                .as("인증된 일반 요청에서 XSRF-TOKEN 이 교체/삭제되면 안 된다")
                .noneMatch(c -> c.startsWith("XSRF-TOKEN="));
    }
}
