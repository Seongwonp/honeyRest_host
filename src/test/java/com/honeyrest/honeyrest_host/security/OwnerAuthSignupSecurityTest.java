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

        // Spring Security 6의 기본 CsrfTokenRequestHandler(XorCsrfTokenRequestAttributeHandler)는
        // BREACH 공격 방어를 위해 화면에 노출하는 토큰 값을 매 요청 XOR로 마스킹한다. 즉 쿠키에 저장된
        // 원본 값과 폼/헤더로 제출하는 값이 서로 다르다(같은 문자열을 넣으면 오히려 실패한다).
        // 실제 브라우저처럼 로그인 페이지의 숨은 input에서 마스킹된 값을 그대로 읽어와야 한다.
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
