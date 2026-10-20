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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
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
// 회사 소유권은 컨트롤러에서 @PreAuthorize("@companyAccess.ownsXxx(...)")로 검사한다. 거부 시 403.
@EnableMethodSecurity
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

    // 직접 new 하면 ServletContext 가 주입되지 않아 "No ServletContext" 예외가 나므로 빈으로 등록한다.
    @Bean
    public MultipartFilter multipartFilter() {
        return new MultipartFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MultipartFilter multipartFilter) throws Exception {
        // 0) 기본 보안 옵션
        // STATELESS 라 세션 저장소 대신 쿠키 기반 CSRF 저장소를 쓴다.
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        // 기본 설정은 JWT 로 인증된 매 요청마다 토큰을 교체해 폼 POST 가 403 이 나므로 로그인 요청에서만 교체한다.
        CsrfAuthenticationStrategy rotateOnLogin = new CsrfAuthenticationStrategy(csrfTokenRepository);
        http.csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository)
                .sessionAuthenticationStrategy((authentication, request, response) -> {
                    if (isLoginSubmit(request)) {
                        rotateOnLogin.onAuthentication(authentication, request, response);
                    }
                }));
        // 멀티파트 요청의 _csrf 파라미터를 읽을 수 있도록 CsrfFilter 앞에서 바디를 파싱한다.
        http.addFilterBefore(multipartFilter, CsrfFilter.class);
        // 토큰은 지연 로딩이라 응답이 커밋된 뒤 읽히면 Set-Cookie 가 빠진다. 커밋 전에 미리 로드한다.
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
                        "/error/**",
                        "/uploads/**",       // 로컬 저장소 모드의 업로드 이미지
                        "/actuator/health"
                ).permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/admin/companies/**").hasRole("SUPER_ADMIN")
                // 업체 가입은 비로그인 공개. 서비스가 role 을 COMPANY_ADMIN 으로 고정한다.
                .requestMatchers("/api/admin/auth/**").permitAll()
                // /api/owner/auth/signup 은 SUPER_ADMIN 계정을 만들 수 있으므로 SUPER_ADMIN 만 호출 가능
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

    /** 폼 로그인 처리 요청(POST /auth/login) 여부 */
    static boolean isLoginSubmit(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && (request.getContextPath() + "/auth/login").equals(request.getRequestURI());
    }
}
