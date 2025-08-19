package com.honeyrest.honeyrest_host.security;

import com.honeyrest.honeyrest_host.config.JwtTokenProvider;
import com.honeyrest.honeyrest_host.entity.enums.RoleType;
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
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String raw = resolveToken(request);         // 헤더/쿠키 모두에서 원문 추출
        String token = normalizeToken(raw);         // Bearer 제거

        log.debug("[JWT] URI={}, rawToken={}, normalizedToken={}",
                request.getRequestURI(),
                raw != null ? mask(raw) : null,
                token != null ? mask(token) : null);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long userId = jwtTokenProvider.getUserId(token);
            RoleType role = jwtTokenProvider.getRole(token);

            if (userId != null && role != null) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("[JWT] Auth set: userId={}, role={}", userId, role);
            } else {
                log.debug("[JWT] Missing userId/role in token");
            }
        } else {
            log.debug("[JWT] No token or invalid token");
        }

        chain.doFilter(request, response);
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
        String uri = request.getRequestURI();
        return uri.startsWith("/api/admin/auth/")
                || uri.startsWith("/admin/auth/")
                || uri.startsWith("/admin/assets/")
                || uri.startsWith("/assets/")
                || uri.startsWith("/static/")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/.well-known/")
                || uri.startsWith("/swagger-ui/")
                || uri.startsWith("/v3/api-docs");
    }
}