package com.honeyrest.honeyrest_host.type;

import java.util.Locale;

/**
 * 업체 상태 문자열 상수 (company.status VARCHAR).
 * 공유 도메인 모듈에 없어 호스트에 둔다. 과거 데이터에 소문자 "active" 가 있어 비교는 대소문자를 무시한다.
 */
public final class CompanyStatus {

    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";

    private CompanyStatus() {
    }

    public static boolean isActive(String status) {
        return status != null && ACTIVE.equals(status.trim().toUpperCase(Locale.ROOT));
    }
}
