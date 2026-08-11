package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import com.honeyrest.honeyrest_host.security.JwtAuthFilter;
import com.honeyrest.honeyrest_host.serviceOwner.OAdminUserDetailsService;
import org.springframework.http.ResponseCookie;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.support.MultipartFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    // http.addFilterBefore(new MultipartFilter(), ...)로 직접 new 하면 GenericFilterBean의
    // ServletContext 초기화 콜백을 받지 못해 "No ServletContext" 예외가 난다.
    // 스프링 빈으로 등록해야 컨테이너가 정상적으로 초기화해 준다.
    @Bean
    public MultipartFilter multipartFilter() {
        return new MultipartFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MultipartFilter multipartFilter) throws Exception {
        // 0) 기본 보안 옵션
        // STATELESS라 세션 기반 CsrfTokenRepository가 동작하지 않으므로 쿠키 기반 저장소를 사용한다.
        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        // multipart/form-data(이미지 업로드) 요청은 CsrfFilter가 실행되는 시점에 바디가 아직
        // 파싱되지 않아 request.getParameter("_csrf")가 비어 항상 403이 난다.
        // CsrfFilter보다 앞에서 멀티파트 바디를 먼저 파싱하도록 배치한다.
        http.addFilterBefore(multipartFilter, CsrfFilter.class);
        // CsrfToken은 지연 로딩되어 뷰에서 실제로 값을 읽는 시점에야 쿠키가 저장된다.
        // 응답 바디(사이드바 등)가 기본 응답 버퍼(8KB)를 넘겨 커밋된 뒤에 토큰을 읽으면
        // Set-Cookie가 누락되어 다음 POST가 403(CSRF 불일치)이 나므로, 필터 체인 초입에서
        // 토큰을 미리 로드해 응답 커밋 전에 쿠키를 확정한다.
        http.addFilterAfter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                             FilterChain filterChain) throws ServletException, IOException {
                CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrfToken != null) {
                    csrfToken.getToken();
                }
                filterChain.doFilter(request, response);
            }
        }, CsrfFilter.class);
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
                // 회사 관리자(신규 업체) 자가 가입/로그인은 비로그인 상태에서 호출되어야 하므로 명시적으로 공개한다.
                // AdminAuthService가 role을 항상 COMPANY_ADMIN으로 고정하므로 여기서 권한 상승은 발생하지 않는다.
                .requestMatchers("/api/admin/auth/**").permitAll()
                // /api/owner/auth/signup은 SUPER_ADMIN 계정을 만들 수 있는 엔드포인트이므로
                // /owner/** 와 별개로 반드시 이미 SUPER_ADMIN으로 인증된 사용자만 호출할 수 있어야 한다.
                // (이전에는 이 경로가 어떤 규칙에도 매칭되지 않아 anyRequest().authenticated()로 떨어졌고,
                //  Spring Security 기본 설정상 익명 사용자까지 통과시켜 role=SUPER_ADMIN 자가 발급이 가능했다.)
                .requestMatchers("/api/owner/**").hasRole("SUPER_ADMIN")
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

                    // 2-3) 쿠키에 심기
                    ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", token)
                            .httpOnly(true)
                            .path("/")
                            .secure("https".equalsIgnoreCase(req.getScheme()))
                            .sameSite("Lax")
                            .build();
                    res.addHeader("Set-Cookie", cookie.toString());

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