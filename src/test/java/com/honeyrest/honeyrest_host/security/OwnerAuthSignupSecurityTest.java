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
 * /api/owner/auth/signup 은 SUPER_ADMIN 만 호출할 수 있어야 한다 (COMPANY_ADMIN·익명 사용자 거부).
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
        // 미인증 요청은 로그인 페이지로 리다이렉트된다. 상태 코드보다 계정이 생성되지 않았는지를 본다.
        ResponseEntity<String> response = attemptSignup(null, "SUPER_ADMIN", "anon-super");

        assertTrue(response.getStatusCode() != HttpStatus.CREATED,
                "익명 요청으로 계정이 생성됨: " + response.getStatusCode());
        assertTrue(response.getBody() == null || !response.getBody().contains("보안 테스트"),
                "signup 컨트롤러가 익명 요청에도 응답 본문을 생성함 (실제로 처리됐을 가능성)");
    }

    @Test
    void superAdmin은_새_관리자를_생성할_수_있다() {
        String token = jwtTokenProvider.createAccessToken(2L, "super-admin@test.local", "SUPER_ADMIN");

        // Spring Security 6 는 화면의 CSRF 토큰을 XOR 마스킹하므로 쿠키 값이 아니라 로그인 페이지의 hidden input 값을 쓴다.
        ResponseEntity<String> loginPage = restTemplate.getForEntity("/auth/login", String.class);
        String xsrfCookie = loginPage.getHeaders().get(HttpHeaders.SET_COOKIE).stream()
                .filter(c -> c.startsWith("XSRF-TOKEN="))
                .findFirst()
                .map(c -> c.substring("XSRF-TOKEN=".length()).split(";")[0])
                .orElseThrow(() -> new IllegalStateException("XSRF-TOKEN 쿠키를 받지 못함"));
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("name=\"_csrf\"\\s+value=\"([^\"]+)\"")
                .matcher(loginPage.getBody());
        if (!m.find()) {
            throw new IllegalStateException("로그인 페이지에서 _csrf 히든 필드를 찾지 못함");
        }
        String csrfHeaderValue = m.group(1);

        ResponseEntity<String> response = attemptSignup(token, xsrfCookie, csrfHeaderValue, "COMPANY_ADMIN", "created-by-super");

        assertEquals(HttpStatus.CREATED, response.getStatusCode(),
                "body=" + response.getBody());
    }

    private ResponseEntity<String> attemptSignup(String accessToken, String targetRole, String emailPrefix) {
        return attemptSignup(accessToken, null, null, targetRole, emailPrefix);
    }

    private ResponseEntity<String> attemptSignup(String accessToken, String csrfCookieValue, String csrfHeaderValue,
                                                  String targetRole, String emailPrefix) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        StringBuilder cookie = new StringBuilder();
        if (accessToken != null) {
            cookie.append("ACCESS_TOKEN=").append(accessToken);
        }
        if (csrfCookieValue != null) {
            if (!cookie.isEmpty()) cookie.append("; ");
            cookie.append("XSRF-TOKEN=").append(csrfCookieValue);
            headers.add("X-XSRF-TOKEN", csrfHeaderValue);
        }
        if (!cookie.isEmpty()) {
            headers.add(HttpHeaders.COOKIE, cookie.toString());
        }
        String body = """
                {"email":"%s@test.local","password":"test1234","name":"보안 테스트","role":"%s"}
                """.formatted(emailPrefix, targetRole);
        return restTemplate.exchange("/api/owner/auth/signup", HttpMethod.POST,
                new HttpEntity<>(body, headers), String.class);
    }
}
