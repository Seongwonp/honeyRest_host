package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.entity.enums.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:3600000}") // 기본 1시간
    private long expirationMs;

    @Value("${jwt.refresh-expiration-ms:1209600000}") // 기본 14일
    private long refreshExpirationMs;

    @Value("${jwt.issuer:honeyrest-admin}")
    private String issuer;

    // ===== claim 키 상수 =====
    private static final String C_ROLE  = "role";   // RoleType.name()
    private static final String C_EMAIL = "email";  // 관리 콘솔 편의 추출용
    private static final String C_TYP   = "typ";    // 토큰 유형 (access/refresh 등)

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    /*---------------생성 -------------*/
    /** 관리자/유저 공통 AccessToken 생성 (userId, email, role) */
    public String createAccessToken(Long userId, String email, RoleType roleType) {
        return buildToken(
                Map.of(
                        C_TYP, "access",
                        C_EMAIL, email,
                        C_ROLE, roleType.name()
                ),
                String.valueOf(userId),
                Duration.ofMillis(expirationMs)
        );
    }

    /* (선택) RefreshToken 생성 */
    public String createRefreshToken(Long userId, String email, RoleType role) {
        return buildToken(
                Map.of(
                        C_TYP, "refresh",
                        C_EMAIL, email,
                        C_ROLE, role.name()
                ),
                String.valueOf(userId),
                Duration.ofMillis(refreshExpirationMs)
        );
    }

    /* 내부 공통 빌더 */
    private String buildToken(Map<String, Object> claims, String subject, Duration ttl) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttl.toMillis());

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)           // userId
                .addClaims(claims)             // role/email/typ
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ---------------- 파싱/검증 ---------------- */

    public Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(key())
                .build()
                .parseSignedClaims(token);
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Authorization: Bearer ... 추출
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    // ---------------- 편의 Getter ----------------

    /* sub = userId */
    public Long getUserId(String token) {
        String sub = parseClaims(token).getPayload().getSubject();
        return (sub == null) ? null : Long.valueOf(sub);
    }

    public RoleType getRole(String token) {
        String r = getClaim(token, C_ROLE, String.class);
        return (r == null) ? null : RoleType.valueOf(r);
    }

    public String getEmail(String token) {
        return getClaim(token, C_EMAIL, String.class);
    }

    public String getType(String token) {
        return getClaim(token, C_TYP, String.class); // access / refresh
    }

    public <T> T getClaim(String token, String key, Class<T> type) {
        Object value = parseClaims(token).getPayload().get(key);
        if (value == null) return null;
        return type.cast(value);
    }

    // 기존 메서드 이름 유지용 (호환)
    public boolean validateToken(String token) { return validate(token); }
    public String getSubject(String token) { return parseClaims(token).getPayload().getSubject(); }
}