package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class DataInitializer implements CommandLineRunner {

    private final OUserRepository oUserRepository;
    private final PasswordEncoder passwordEncoder;

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
                        .passwordHash(passwordEncoder.encode("company1234"))
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
                    .passwordHash(passwordEncoder.encode("admin1234"))
                    .name("HoneyRest관리자")
                    .role("SUPER_ADMIN")
                    .status("ACTIVE")
                    .isVerified(true)
                    .build());
            created++;
        }

        log.info("DataInitializer: created {} accounts", created);
    }
}
