package com.honeyrest.honeyrest_host.config;

import com.honeyrest.domain.entity.User;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import com.honeyrest.honeyrest_host.serviceCommon.refund.NoOpTossCancelClient;
import com.honeyrest.honeyrest_host.serviceCommon.refund.TossCancelClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * demo 프로필(Render 무료 데모)이 MySQL·Redis 없이 기동하고, 데모 계정이 환경변수 비밀번호로 만들어지며
 * 로그인 화면에 그 계정이 안내되는지 검증한다. (render.yaml 이 주는 DEMO_* 환경변수를 그대로 흉내 낸다)
 */
@SpringBootTest(properties = {
        "DEMO_COMPANY_PASSWORD=Demo1234!",
        "DEMO_ADMIN_PASSWORD=Demo1234!",
        "APP_STORAGE_LOCAL_DIR=build/test-uploads-demo"
})
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@DisplayName("demo 프로필: H2 + 시드로 기동, 데모 계정 생성·로그인 화면 안내")
class DemoProfileStartupTest {

    @Autowired private Environment env;
    @Autowired private OUserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private TossCancelClient tossCancelClient;
    @Autowired private MockMvc mockMvc;

    @Test
    @DisplayName("Render 용 설정: PORT 수신, 프록시 헤더 신뢰, Redis 끔, 파일 로그 없음, PG 취소 No-op")
    void demoSettings() {
        assertThat(env.getProperty("server.port")).isEqualTo("8081");
        assertThat(env.getProperty("server.forward-headers-strategy")).isEqualTo("framework");
        assertThat(env.getProperty("app.cache.redis.enabled")).isEqualTo("false");
        assertThat(env.getProperty("logging.file.name")).isEmpty();
        assertThat(tossCancelClient).isInstanceOf(NoOpTossCancelClient.class);
    }

    @Test
    @DisplayName("데모 계정(업체 관리자·최고 관리자)이 환경변수 비밀번호로 만들어진다")
    void demoAccountsCreated() {
        User company = userRepository.findByEmail("contact@honeyrest.com");
        User admin = userRepository.findByEmail("admin@honeyrest.com");
        assertThat(company.getRole()).isEqualTo("COMPANY_ADMIN");
        assertThat(admin.getRole()).isEqualTo("SUPER_ADMIN");
        assertThat(passwordEncoder.matches("Demo1234!", company.getPasswordHash())).isTrue();
        assertThat(passwordEncoder.matches("Demo1234!", admin.getPasswordHash())).isTrue();
    }

    @Test
    @DisplayName("로그인 화면에 두 데모 계정이 안내된다")
    void loginPageShowsDemoAccounts() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("demoAccounts", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(content().string(containsString("data-testid=\"demo-accounts\"")))
                .andExpect(content().string(containsString("contact@honeyrest.com")))
                .andExpect(content().string(containsString("admin@honeyrest.com")));
    }

    @Test
    @DisplayName("비밀번호가 비어 있는 계정은 안내하지 않는다")
    void accountsWithoutPasswordAreHidden() {
        DemoLoginProperties props = new DemoLoginProperties();
        DemoLoginProperties.Account withPassword = new DemoLoginProperties.Account();
        withPassword.setEmail("a@example.com");
        withPassword.setPassword("pw");
        DemoLoginProperties.Account withoutPassword = new DemoLoginProperties.Account();
        withoutPassword.setEmail("b@example.com");
        withoutPassword.setPassword("");
        props.setLoginAccounts(List.of(withPassword, withoutPassword));

        assertThat(props.visibleAccounts()).extracting(DemoLoginProperties.Account::getEmail).containsExactly("a@example.com");
    }
}
