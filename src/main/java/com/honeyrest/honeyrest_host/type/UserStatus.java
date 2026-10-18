package com.honeyrest.honeyrest_host.type;

import java.util.Locale;

/** 회원(관리자 계정 포함) 상태 문자열 상수 (user.status VARCHAR). */
public final class UserStatus {

    public static final String ACTIVE = "ACTIVE";

    private UserStatus() {
    }

    public static boolean isActive(String status) {
        return status != null && ACTIVE.equals(status.trim().toUpperCase(Locale.ROOT));
    }
}
