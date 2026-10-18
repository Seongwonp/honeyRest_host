package com.honeyrest.honeyrest_host.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 로그인 화면에 안내할 데모 계정 목록 ({@code app.demo.login-accounts[n].*}).
 * <p>
 * demo 프로필(application-demo.properties)에서만 값을 채운다. 다른 프로필은 목록이 비어 있어 로그인 화면에 아무것도 나오지 않는다.
 * 비밀번호가 비어 있는 계정(= DataInitializer 가 만들지 않은 계정)은 안내하지 않는다.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.demo")
public class DemoLoginProperties {

    private List<Account> loginAccounts = new ArrayList<>();

    /** 안내 가능한(이메일·비밀번호가 모두 있는) 계정만 */
    public List<Account> visibleAccounts() {
        return loginAccounts.stream()
                .filter(a -> hasText(a.getEmail()) && hasText(a.getPassword()))
                .toList();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    @Getter
    @Setter
    public static class Account {
        /** 화면 표시용 역할 이름 (예: 업체 관리자) */
        private String label;
        private String email;
        private String password;
    }
}
