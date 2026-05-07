package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import com.honeyrest.honeyrest_host.security.JwtAuthFilter;
import com.honeyrest.honeyrest_host.serviceOwner.OAdminUserDetailsService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAdminUserDetailsService adminUserDetailsService;
    private final OUserRepository userRepository;

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
        // 0) 기본 보안 옵션
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 1) 인가 규칙
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico",
                        "/.well-known/**",
                        "/auth/**",
                        "/error/**"
                ).permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/admin/customers/**", "/api/admin/companies/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/admin/**").hasRole("COMPANY_ADMIN")
                .requestMatchers("/owner/**").hasRole("SUPER_ADMIN")
                .anyRequest().authenticated()
        );

        // 2) 폼 로그인 (처리 URL은 하나만!  /auth/login 으로 통일)
        http.formLogin(form -> form
                // 로그인 페이지는 하나만 지정 (둘을 동시에 쓰고 싶다면 체인 분리 필요)
                .loginPage("/auth/login") // 기본 미인증 리다이렉트 페이지 (원하면 공용 /auth/login으로 바꿔도 됨)
                .loginProcessingUrl("/auth/login")   // 처리 URL 하나만! (폼 action을 여기에 맞추세요)
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
                .successHandler((req, res, auth) -> {
                    // 2-1) 사용자 조회
                    final String email = auth.getName();
                    var user = userRepository.findByEmail(email);
                    if (user == null) {    // NPE 방지
                        res.sendRedirect("/auth/login?error=NO_USER");
                        return;
                    }

                    // 2-2) JWT 발급
                    String token = jwtTokenProvider.createAccessToken(
                            user.getUserId(),
                            user.getEmail(),
                            user.getRole()   // ex) "COMPANY_ADMIN" or "SUPER_ADMIN"
                    );

                    // 2-3) 쿠키에 심기 (필요 시 Secure=true/MaxAge 설정)
                    Cookie c = new Cookie("ACCESS_TOKEN", token);
                    c.setHttpOnly(true);
                    c.setPath("/");
                    c.setSecure("https".equalsIgnoreCase(req.getScheme()));
                    res.addCookie(c);

                    // 2-4) SecurityContext 갱신 (STATELESS라도 이후 필터에서 참조 가능)
                    var springAuth = new UsernamePasswordAuthenticationToken(
                            email, null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                    );
                    SecurityContextHolder.getContext().setAuthentication(springAuth);

                    // 2-5) 역할 분기 (if로 한 번만 리다이렉트!)
                    if ("COMPANY_ADMIN".equals(user.getRole())) {
                        res.sendRedirect("/admin/dashboard");
                    } else if ("SUPER_ADMIN".equals(user.getRole())) {
                        res.sendRedirect("/owner/dashboard");
                    } else {
                        res.sendRedirect("/"); // fallback
                    }
                })
                .failureHandler((req, res, ex) -> res.sendRedirect("/auth/login?error"))
        );

        // 3) 로그아웃 (리다이렉트도 if로 하나만 선택)
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("ACCESS_TOKEN", "JSESSIONID")
                .invalidateHttpSession(true)
                .logoutSuccessHandler((req, res, auth) -> {
                    // 로그인 페이지를 역할별로 다르게 보내고 싶다면, 여기서도 분기 가능
                    // (세션이 이미 비어 있을 수 있으니 파라미터 등으로 분기하거나 공용 페이지로 보냄)
                    res.sendRedirect("/auth/login?logout");
                })
        );

        // 4) JWT 필터
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}