package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

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
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico",
                        "/swagger-ui/**", "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/admin/**").permitAll()
                .requestMatchers("/admin/**").hasAnyRole("COMPANY_ADMIN", "SUPER_ADMIN")
                .requestMatchers("/admin/auth/**", "/api/admin/auth/**").permitAll()
                .anyRequest().permitAll()
        );

        // ===== CSP 헤더 설정 (Daum Postcode + Kakao Map + jsDelivr) =====
        http.headers(headers -> {
            headers.contentSecurityPolicy(csp -> csp.policyDirectives(
                    String.join(" ",
                            "default-src 'self'; " +
                                    "script-src 'self' 'unsafe-inline' https://t1.daumcdn.net https://ssl.daumcdn.net https://dapi.kakao.com; " +
                                    "img-src 'self' data: https://t1.daumcdn.net https://*.kakaocdn.net blob:; " +
                                    "connect-src 'self' https://dapi.kakao.com; " +
                                    "style-src 'self' 'unsafe-inline'; " +
                                    "font-src 'self' data:; " +
                                    "frame-src 'self' https://postcode.map.daum.net http://postcode.map.daum.net https://t1.daumcdn.net http://t1.daumcdn.net; " +
                                    "object-src 'none'; frame-ancestors 'self';"
                    )
            ));

            headers.referrerPolicy(rp ->
                    rp.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN));

            headers.addHeaderWriter(new org.springframework.security.web.header.writers.StaticHeadersWriter(
                    "Permissions-Policy", "geolocation=(self)"
            ));
        });

        // JWT 필터 등…
        // http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}