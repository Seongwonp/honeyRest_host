package com.honeyrest.honeyrest_host.config;

import com.honeyrest.domain.entity.User;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 데모/로컬 시연용 계정 시더. 과거에는 프로필 제한이 없어 모든 환경(운영 포함)에서
 * 고정 비밀번호로 SUPER_ADMIN 계정을 만들었다(P0-1). local-demo 프로필을 명시적으로
 * 활성화한 경우에만 실행되도록 opt-in으로 전환했다.
 * <p>
 * 운영 배포에서 데모 계정이 필요하면 {@code prod,local-demo} 로 켜고 비밀번호를 환경변수
 * {@code DEMO_COMPANY_PASSWORD} / {@code DEMO_ADMIN_PASSWORD} 로 준다 (application-prod.properties).
 * 비밀번호가 비어 있으면 해당 계정 묶음은 만들지 않는다 → 빈 비밀번호·코드 기본값 계정이 생기지 않는다.
 * 이미 있는 계정은 건드리지 않으므로(비밀번호 변경 없음) 재기동해도 안전하다.
 */
@Component
@RequiredArgsConstructor
@Log4j2
@Profile("local-demo")
public class DataInitializer implements CommandLineRunner {

    private final OUserRepository oUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${demo.company-admin.password:company1234}")
    private String companyAdminPassword;

    @Value("${demo.super-admin.password:admin1234}")
    private String superAdminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        int created = 0;
        boolean createCompanyAccounts = hasText(companyAdminPassword, "demo.company-admin.password");
        boolean createSuperAdmin = hasText(superAdminPassword, "demo.super-admin.password");

        String[][] companyAccounts = {
                {"contact@honeyrest.com", "박성원"},
                {"info@seasidehotel.com", "김바다"},
                {"info@urbanstay.com", "이도시"},
                {"info@hanokhospitality.com", "박한옥"},
                {"info@natureretreat.com", "최자연"},
                {"info@gyeongjustay.com", "김경주"}
        };

        for (String[] account : createCompanyAccounts ? companyAccounts : new String[0][]) {
            String email = account[0];
            String name = account[1];
            if (oUserRepository.findByEmail(email) == null) {
                oUserRepository.save(User.builder()
                        .email(email)
                        .passwordHash(passwordEncoder.encode(companyAdminPassword))
                        .name(name)
                        .role("COMPANY_ADMIN")
                        .status("ACTIVE")
                        .isVerified(true)
                        .build());
                created++;
            }
        }

        if (createSuperAdmin && oUserRepository.findByEmail("admin@honeyrest.com") == null) {
            oUserRepository.save(User.builder()
                    .email("admin@honeyrest.com")
                    .passwordHash(passwordEncoder.encode(superAdminPassword))
                    .name("HoneyRest관리자")
                    .role("SUPER_ADMIN")
                    .status("ACTIVE")
                    .isVerified(true)
                    .build());
            created++;
        }

        log.info("DataInitializer(local-demo): created {} accounts", created);
    }

    private static boolean hasText(String password, String property) {
        if (password == null || password.isBlank()) {
            log.warn("DataInitializer(local-demo): {} 가 비어 있어 해당 데모 계정 생성을 건너뜁니다.", property);
            return false;
        }
        return true;
    }
}
