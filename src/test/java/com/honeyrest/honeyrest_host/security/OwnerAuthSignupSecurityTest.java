package com.honeyrest.honeyrest_host.security;

import com.honeyrest.honeyrest_host.config.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * P0-2 회귀 테스트: COMPANY_ADMIN이 /api/owner/auth/signup으로 role=SUPER_ADMIN 계정을
 * 자가 발급할 수 없어야 하고, 익명 사용자도 호출할 수 없어야 한다. SUPER_ADMIN만 허용된다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OwnerAuthSignupSecurityTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void companyAdmin은_super_admin으로_자가_승격할_수_없다() {
        String token = jwtTokenProvider.createAccessToken(1L, "company-admin@test.local", "COMPANY_ADMIN");

        ResponseEntity<String> response = attemptSignup(token, "SUPER_ADMIN", "escalated-super");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void 익명_사용자는_owner_signup을_호출할_수_없다() {
        // 미인증 요청은 formLogin 설정상 /auth/login으로 302 리다이렉트되고
        // TestRestTemplate은 기본적으로 리다이렉트를 따라가 최종 200(로그인 페이지)을 반환한다.
        // 중요한 건 최종 상태 코드가 아니라 signup 컨트롤러가 실제로는 호출되지 않아 계정이 생성되지 않는 것이다.
        ResponseEntity<String> response = attemptSignup(null, "SUPER_ADMIN", "anon-super");

        assertTrue(response.getStatusCode() != HttpStatus.CREATED,
                "익명 요청으로 계정이 생성됨: " + response.getStatusCode());
        assertTrue(response.getBody() == null || !response.getBody().contains("보안 테스트"),
                "signup 컨트롤러가 익명 요청에도 응답 본문을 생성함 (실제로 처리됐을 가능성)");
    }

    @Test
    void superAdmin은_새_관리자를_생성할_수_있다() {
        String token = jwtTokenProvider.createAccessToken(2L, "super-admin@test.local", "SUPER_ADMIN");

        ResponseEntity<String> response = attemptSignup(token, "COMPANY_ADMIN", "created-by-super");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    private ResponseEntity<String> attemptSignup(String accessToken, String targetRole, String emailPrefix) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null) {
            headers.add(HttpHeaders.COOKIE, "ACCESS_TOKEN=" + accessToken);
        }
        String body = """
                {"email":"%s@test.local","password":"test1234","name":"보안 테스트","role":"%s"}
                """.formatted(emailPrefix, targetRole);
        return restTemplate.exchange("/api/owner/auth/signup", HttpMethod.POST,
                new HttpEntity<>(body, headers), String.class);
    }
}
