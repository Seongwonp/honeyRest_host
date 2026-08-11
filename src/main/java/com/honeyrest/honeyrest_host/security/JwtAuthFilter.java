package com.honeyrest.honeyrest_host.security;

import com.honeyrest.honeyrest_host.config.JwtTokenProvider;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    private static final AntPathMatcher PM = new AntPathMatcher();
    private static final String[] WHITELIST = {
            "/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico",
            "/swagger-ui/**", "/v3/api-docs/**",
            "/.well-known/**",
            "/auth/**"   // 로그인/로그아웃/페이지
    };

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();
        try {
            String token = jwtTokenProvider.resolveToken(request);

            if (token != null && jwtTokenProvider.validate(token)) {
                Jws<Claims> jws = jwtTokenProvider.parseClaims(token);
                Claims claims = jws.getPayload();

                String email = (String) claims.get("email");
                String roleStr = (String) claims.get("role");
                String typ = (String) claims.get("typ");

                // refresh token은 typ 검증 없이도 access token과 동일하게 인증에 통용됐다(P1-7).
                // refresh token은 14일짜리라, 유출 시 access token 1시간 만료 정책이 무의미해진다.
                if (email != null && roleStr != null && "access".equals(typ)) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            email, null, List.of(new SimpleGrantedAuthority("ROLE_" + roleStr)));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else if (email != null && roleStr != null) {
                    log.warn("[JWT] {} refresh(또는 typ 불명) 토큰으로 인증 시도 차단: typ={}", uri, typ);
                }
            }
        } catch (Exception e) {
            log.warn("[JWT] {} token validation failed: {}", uri, e.getClass().getSimpleName());
        }


        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String p : WHITELIST) {
            if (PM.match(p, path)) return true;
        }
        return false;
    }
}