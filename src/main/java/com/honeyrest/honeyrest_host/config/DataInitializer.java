package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.type.UserStatus;

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
 * 데모용 계정 시더. local-demo·demo 프로필에서만 실행된다(opt-in).
 * 비밀번호는 DEMO_COMPANY_PASSWORD / DEMO_ADMIN_PASSWORD 환경변수로 주며, 비어 있으면 해당 계정은 만들지 않는다.
 * 이미 있는 계정은 건드리지 않는다.
 */
@Component
@RequiredArgsConstructor
@Log4j2
@Profile({"local-demo", "demo"})
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
                        .status(UserStatus.ACTIVE)
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
                    .status(UserStatus.ACTIVE)
                    .isVerified(true)
                    .build());
            created++;
        }

        log.info("DataInitializer(local-demo/demo): created {} accounts", created);
    }

    private static boolean hasText(String password, String property) {
        if (password == null || password.isBlank()) {
            log.warn("DataInitializer(local-demo): {} 가 비어 있어 해당 데모 계정 생성을 건너뜁니다.", property);
            return false;
        }
        return true;
    }
}
