package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.AdminSignupRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.TokenResponseDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.AdminAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AdminAuthService adminAuthService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody AdminSignupRequestDTO adminSignupRequestDTO) {
        adminAuthService.adminSignup(adminSignupRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AdminLoginRequestDTO request,
                                      HttpServletResponse res) {
        TokenResponseDTO token = adminAuthService.login(request);

        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", token.getAccessToken())
                .httpOnly(true)          // JS에서 접근 불가(보안상 권장)
                .secure(false)           // HTTPS면 true
                .sameSite("Lax")         // Top-level same-site 이동에서 전송됨
                .path("/")
                .maxAge(Duration.ofMillis(token.getExpiresIn()))
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build(); // 프런트에서 200이면 /admin/dashboard로 이동
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse res) {
        // 쿠키 제거
        ResponseCookie del = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true).path("/").maxAge(0).build();
        res.addHeader(HttpHeaders.SET_COOKIE, del.toString());
        return ResponseEntity.noContent().build();
    }
}
