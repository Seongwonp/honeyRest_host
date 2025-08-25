package com.honeyrest.honeyrest_host.security;

import com.honeyrest.honeyrest_host.config.JwtTokenProvider;
import com.honeyrest.honeyrest_host.repository.UserRepository;
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
import jakarta.servlet.http.Cookie;

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
            "/admin/auth/**"   // 로그인/로그아웃/페이지
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
            log.debug("[JWT] {} token? {}", uri, token != null);

            if (token != null && jwtTokenProvider.validate(token)) {
                Jws<Claims> jws = jwtTokenProvider.parseClaims(token);
                Claims claims = jws.getPayload();

                String email = (String) claims.get("email");
                String roleStr = (String) claims.get("role");

                log.debug("[JWT] {} email={}, role={}", uri, email, roleStr);

                if (email != null && roleStr != null) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            email, null, List.of(new SimpleGrantedAuthority("ROLE_" + roleStr)));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } else {
                log.debug("[JWT] {} token missing or invalid", uri);
            }
        } catch (Exception e) {
            log.warn("[JWT] {} exception: {}", uri, e.toString());
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        // 1) Authorization: Bearer ...
        String bearer = request.getHeader("Authorization");
        if (bearer != null && !bearer.isBlank()) {
            return bearer;
        }
        // 2) Cookie: ACCESS_TOKEN
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(c.getName())) {
                    return c.getValue(); // 여기 값은 보통 순수 JWT
                }
            }
        }
        return null;
    }

    private String normalizeToken(String raw) {
        if (raw == null) return null;
        String v = raw.trim();
        if (v.length() >= 7 && v.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return v.substring(7).trim();
        }
        return v;
    }

    private String mask(String v) {
        if (v == null) return null;
        return (v.length() <= 12) ? v : v.substring(0, 6) + "..." + v.substring(v.length() - 6);
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