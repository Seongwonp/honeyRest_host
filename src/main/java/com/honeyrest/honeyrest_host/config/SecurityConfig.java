package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import com.honeyrest.honeyrest_host.security.JwtAuthFilter;
import com.honeyrest.honeyrest_host.service.AdminUserDetailsService;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
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
        log.info("==============1========");
        http.csrf(csrf -> csrf.disable());
        // 세션은 쓰지 않고 JWT로만
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**",
                                "/.well-known/**",
                                "/admin/auth/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("COMPANY_ADMIN", "SUPER_ADMIN")
                        .anyRequest().permitAll()



        );
        log.info("==============2========");

        // ✅ 폼 로그인: username 파라미터를 email로 바꾸고, 성공 시 JWT 쿠키 심기
        http.formLogin(form -> form
                        .loginPage("/admin/auth/login")
                        .loginProcessingUrl("/admin/auth/login")
                        .usernameParameter("email")            // 폼 input name="email"
                        .passwordParameter("password")         // 폼 input name="password"
                        .permitAll()
                        .successHandler((req, res, auth) -> {


                            // Principal의 username == email
                            String email = auth.getName();
                            log.info("==============3========");
                            // 필요하면 DB에서 user 조회하여 id/role 꺼내세요 (email->user)
                            // 2) DB에서 사용자 조회
                            var user = userRepository.findByEmail(email);
                            log.info("==============4========");

                            // 간단히 role은 GrantedAuthority에서 읽어도 됨: auth.getAuthorities()

                            // 여기서는 email만으로 토큰 만들 수 없으니, JwtTokenProvider에 email->user 조회 로직이 없다면
                            // userId, role을 서비스/리포지토리로 조회해 채워주세요.
                            // 예시로 email만 넣는 오버로드가 있다치면 교체하세요.
                            String token = jwtTokenProvider.createAccessToken(
                                    user.getUserId(),
                                    user.getEmail(),
                                    user.getRole()
                            );
                            log.info("==============5========");


                            Cookie c = new Cookie("ACCESS_TOKEN", token);
                            c.setHttpOnly(true);
                            c.setPath("/");            // 전체 경로에서 전송
                            // 개발환경이면 Secure=false, 운영 HTTPS면 true 권장
                            c.setSecure(false);
                            // 유효기간 (옵션) - 브라우저 세션쿠키로 두고 싶으면 생략
                            // c.setMaxAge(3600);
                            res.addCookie(c);

                            var springAuth = new UsernamePasswordAuthenticationToken(
                                    email, null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
                            SecurityContextHolder.getContext().setAuthentication(springAuth);

                            res.sendRedirect("/admin/dashboard");

                        })

        );

        // ✅ 로그아웃 설정 추가
        http.logout(logout -> logout
                .logoutUrl("/logout")                       // 로그아웃 처리 URL
                .deleteCookies("ACCESS_TOKEN", "JSESSIONID") // JWT/세션 쿠키 삭제
                .invalidateHttpSession(true)                // 세션 무효화
                .logoutSuccessHandler((req, res, auth) -> {
                    // 로그아웃 성공 시 알림 → 로그인 페이지로 이동
                    res.sendRedirect("/admin/auth/login?logout");
                })
        );


        // JWT 필터 위치
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // CSP/Headers 등 기존 설정이 있다면 여기에 이어서…

        return http.build();
    }
}

