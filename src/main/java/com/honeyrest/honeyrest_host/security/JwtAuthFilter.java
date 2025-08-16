package com.honeyrest.honeyrest_host.security;

import com.honeyrest.honeyrest_host.config.JwtTokenProvider;
import com.honeyrest.honeyrest_host.entity.enums.RoleType;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

            String token = jwtTokenProvider.resolveToken(request);
            if (token != null && jwtTokenProvider.validate(token)) {
                Long userId = Long.valueOf(jwtTokenProvider.getSubject(token));
                RoleType roleType = jwtTokenProvider.getRole(token);

                // ADMIN 전용 보호를 위해 ROLE_ 접두 권한 생성
                List<GrantedAuthority> auths = List.of(new SimpleGrantedAuthority("ROLE_" + roleType.name()));
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, null, auths);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        }

        @Override
        protected boolean shouldNotFilter (HttpServletRequest request){
            String uri = request.getRequestURI();
            return uri.startsWith("/api/admin/auth/") || uri.startsWith("/swagger-ui/") || uri.startsWith("/v3/api-docs");
        }
    }
