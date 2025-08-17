package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; // 필터 주입

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        // JWT 사용 시 세션을 만들지 않는 것을 권장
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                // 1) 정적 리소스는 항상 허용 (맨 위에!)
                .requestMatchers(
                        "/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico",
                        "/swagger-ui/**", "/v3/api-docs/**"
                ).permitAll()

                // 2) 개발 중: 관리자 페이지 '보기'(GET)만 허용
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/admin/**").permitAll()

                // 3) 그 외 /admin/** 는 권한 필요
                .requestMatchers("/admin/**").hasAnyRole("COMPANY_ADMIN", "SUPER_ADMIN")

                // 4) 로그인/회원가입 페이지 & API
                .requestMatchers("/admin/auth/**", "/api/admin/auth/**").permitAll()

                // 5) 나머지
                .anyRequest().permitAll()
        );
        http.headers(headers -> {
            headers.contentSecurityPolicy(csp -> csp.policyDirectives(String.join(" ",
                    "default-src 'self';",
                    // JS
                    "script-src 'self' 'unsafe-inline' https://t1.daumcdn.net https://ssl.daumcdn.net https://cdn.jsdelivr.net;",
                    // CSS
                    "style-src 'self' 'unsafe-inline';",
                    // 이미지
                    "img-src 'self' data: https://t1.daumcdn.net https://ssl.daumcdn.net https://map.pstatic.net https://ssl.pstatic.net;",
                    // 폰트
                    "font-src 'self' data:;",
                    // iframe (다음 주소)
                    "frame-src https://postcode.map.daum.net https://t1.daumcdn.net https://ssl.daumcdn.net;",
                    // fetch
                    "connect-src 'self';",
                    // clickjacking
                    "frame-ancestors 'self';"
            )));
            // Referrer-Policy 설정
            headers.referrerPolicy(rp -> rp.policy(
                    org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN
            ));
            headers.addHeaderWriter(new org.springframework.security.web.header.writers.StaticHeadersWriter(
                    "Permissions-Policy", "geolocation=(self)"
            ));
        });

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}