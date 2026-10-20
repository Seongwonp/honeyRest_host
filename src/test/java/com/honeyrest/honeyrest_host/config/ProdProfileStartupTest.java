package com.honeyrest.honeyrest_host.config;

import com.honeyrest.domain.entity.User;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * prod 프로필이 필수 환경변수만으로 기동되는지, prod,local-demo 에서 데모 계정이 환경변수 비밀번호로만 만들어지는지 검증한다.
 * DEMO_ADMIN_PASSWORD 를 주지 않아 총관리자 계정은 건너뛰어야 한다. DB 는 H2, Redis 는 끈다.
 */
@SpringBootTest(properties = {
        // ---- 운영 compose 가 넣는 필수 환경변수 (값만 테스트용) ----
        "DB_URL=jdbc:h2:mem:honeyrest_host_prod_check;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1",
        "DB_USERNAME=sa",
        "DB_PASSWORD=",
        "JWT_SECRET=prod-profile-test-jwt-secret-0123456789-abcdefghijklmnop",
        "APP_STORAGE_LOCAL_DIR=build/test-uploads-prod",
        "DEMO_COMPANY_PASSWORD=Company-Demo-Pw-1",
        // ---- 이 테스트 환경에 없는 인프라만 대체 ----
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=false",
        "APP_CACHE_REDIS_ENABLED=false"
})
@ActiveProfiles({"prod", "local-demo"})
@DisplayName("prod 프로필: 필수 환경변수만으로 기동, 데모 계정은 환경변수 비밀번호로만 생성")
class ProdProfileStartupTest {

    @Autowired private Environment env;
    @Autowired private OUserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("운영 설정 값이 적용된다")
    void prodValuesResolved() {
        assertThat(env.getProperty("spring.jpa.show-sql")).isEqualTo("false");
        assertThat(env.getProperty("app.storage.type")).isEqualTo("local");
        assertThat(env.getProperty("server.forward-headers-strategy")).isEqualTo("native");
        assertThat(env.getProperty("logging.file.name")).isEmpty();
        // 재고 계산이 옛 스냅샷을 보지 않도록 사용자 API 와 같은 READ COMMITTED
        assertThat(env.getProperty("spring.datasource.hikari.transaction-isolation"))
                .isEqualTo("TRANSACTION_READ_COMMITTED");
    }

    @Test
    @DisplayName("DEMO_COMPANY_PASSWORD 로 업체 관리자 계정이 생성된다")
    void companyDemoAccountsUseEnvPassword() {
        User company = userRepository.findByEmail("contact@honeyrest.com");
        assertThat(company).isNotNull();
        assertThat(company.getRole()).isEqualTo("COMPANY_ADMIN");
        assertThat(passwordEncoder.matches("Company-Demo-Pw-1", company.getPasswordHash())).isTrue();
        assertThat(passwordEncoder.matches("company1234", company.getPasswordHash())).isFalse();
    }

    @Test
    @DisplayName("DEMO_ADMIN_PASSWORD 가 없으면 총관리자 계정을 만들지 않는다")
    void superAdminSkippedWithoutPassword() {
        assertThat(userRepository.findByEmail("admin@honeyrest.com")).isNull();
    }
}
