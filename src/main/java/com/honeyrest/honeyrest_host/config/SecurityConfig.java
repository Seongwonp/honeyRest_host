package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import com.honeyrest.honeyrest_host.security.JwtAuthFilter;
import com.honeyrest.honeyrest_host.service.AdminUserDetailsService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final AdminUserDetailsService adminUserDetailsService;
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(adminUserDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(daoAuthProvider());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        // 세션은 쓰지 않고 JWT로만
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico",
                        "/swagger-ui/**", "/v3/api-docs/**",
                        "/.well-known/**"
                ).permitAll()
                .requestMatchers("/admin/auth/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/admin/**").permitAll()
                .requestMatchers("/admin/**").hasAnyRole("COMPANY_ADMIN", "SUPER_ADMIN")
                .anyRequest().permitAll()
        );

        // ✅ 폼 로그인: username 파라미터를 email로 바꾸고, 성공 시 JWT 쿠키 심기
        http.formLogin(form -> form
                .loginPage("/admin/auth/login")
                .loginProcessingUrl("/admin/auth/login")
                .usernameParameter("email")            // 폼 input name="email"
                .passwordParameter("password")         // 폼 input name="password"
                .successHandler((req, res, auth) -> {
                    // Principal의 username == email
                    String email = auth.getName();
                    // 필요하면 DB에서 user 조회하여 id/role 꺼내세요 (email->user)
                    // 2) DB에서 사용자 조회
                    var user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found: " + email));

                    // 간단히 role은 GrantedAuthority에서 읽어도 됨: auth.getAuthorities()

                    // 여기서는 email만으로 토큰 만들 수 없으니, JwtTokenProvider에 email->user 조회 로직이 없다면
                    // userId, role을 서비스/리포지토리로 조회해 채워주세요.
                    // 예시로 email만 넣는 오버로드가 있다치면 교체하세요.
                    String token = jwtTokenProvider.createAccessToken(
                            user.getUserId(),
                            user.getEmail(),
                            user.getRoleType()
                    );


                    Cookie c = new Cookie("ACCESS_TOKEN", token);
                    c.setHttpOnly(true);
                    c.setPath("/");            // 전체 경로에서 전송
                    // 개발환경이면 Secure=false, 운영 HTTPS면 true 권장
                    c.setSecure(false);
                    // 유효기간 (옵션) - 브라우저 세션쿠키로 두고 싶으면 생략
                    // c.setMaxAge(3600);
                    res.addCookie(c);

                    res.sendRedirect("/admin/dashboard");
                })
//                .failureUrl("/admin/auth/login?error")
//                .permitAll()
        );

        http.logout(l -> l
                .logoutUrl("/admin/auth/logout")
                .logoutSuccessHandler((req, res, auth) -> {
                    // 쿠키 제거
                    Cookie c = new Cookie("ACCESS_TOKEN", "");
                    c.setPath("/");
                    c.setMaxAge(0);
                    res.addCookie(c);
                    res.sendRedirect("/admin/auth/login");
                })
        );

        // JWT 필터 위치
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // CSP/Headers 등 기존 설정이 있다면 여기에 이어서…

        return http.build();
    }
}
/**
 * 2) Admin 웹 체인: FORM LOGIN + 세션 유지 (STATEFUL)
 */
//    @Bean
//    @Order(2)
//    public SecurityFilterChain adminWebChain(HttpSecurity http) throws Exception {
//        http
//            .securityMatcher(
//                "/admin/**",
//                "/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico"
//            )
//            .csrf(csrf -> csrf.disable())
//            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers(
//                    "/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico",
//                    "/swagger-ui/**", "/v3/api-docs/**"
//                ).permitAll()
//                .requestMatchers("/admin/auth/**").permitAll()
//                // ★ 더 이상 GET /admin/** 를 permitAll 하지 않음: 로그인 필요
//                .requestMatchers("/admin/**").hasAnyRole("COMPANY_ADMIN", "SUPER_ADMIN")
//            )
//            .formLogin(form -> form
//                .loginPage("/admin/auth/login")
//                .loginProcessingUrl("/admin/auth/login")
//                    .usernameParameter("email")
//                    .passwordParameter("password")
//                    .defaultSuccessUrl("/admin/dashboard", true)
//                .permitAll()
//            )
//            .logout(l -> l
//                .logoutUrl("/admin/auth/logout")
//                .logoutSuccessUrl("/admin/auth/login")
//            )
//            // ===== CSP 헤더 설정 (Daum Postcode + Kakao Map + jsDelivr) =====
//            .headers(headers -> {
//                headers.contentSecurityPolicy(csp -> csp.policyDirectives(
//                    String.join(" ",
//                        "default-src 'self'; "+
//                        "script-src 'self' 'unsafe-inline' https://t1.daumcdn.net https://ssl.daumcdn.net https://dapi.kakao.com; "+
//                        "img-src 'self' data: https://t1.daumcdn.net https://*.kakaocdn.net blob:; "+
//                        "connect-src 'self' https://dapi.kakao.com; "+
//                        "style-src 'self' 'unsafe-inline'; "+
//                        "font-src 'self' data:; "+
//                        "frame-src 'self' https://postcode.map.daum.net http://postcode.map.daum.net https://t1.daumcdn.net http://t1.daumcdn.net; "+
//                        "object-src 'none'; frame-ancestors 'self';"
//                    )
//                ));
//
//                headers.referrerPolicy(rp ->
//                    rp.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
//                );
//
//                headers.addHeaderWriter(new org.springframework.security.web.header.writers.StaticHeadersWriter(
//                    "Permissions-Policy", "geolocation=(self)"
//                ));
//            });
