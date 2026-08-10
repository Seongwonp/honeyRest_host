package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.entity.User;
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

        String[][] companyAccounts = {
                {"contact@honeyrest.com", "박성원"},
                {"info@seasidehotel.com", "김바다"},
                {"info@urbanstay.com", "이도시"},
                {"info@hanokhospitality.com", "박한옥"},
                {"info@natureretreat.com", "최자연"},
                {"info@gyeongjustay.com", "김경주"}
        };

        for (String[] account : companyAccounts) {
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

        if (oUserRepository.findByEmail("admin@honeyrest.com") == null) {
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
}
