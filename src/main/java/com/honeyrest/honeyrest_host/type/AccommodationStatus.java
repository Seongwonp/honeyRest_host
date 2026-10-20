package com.honeyrest.honeyrest_host.type;

import java.util.Locale;

/**
 * 숙소 상태 문자열 상수 (accommodation.status). 사용자 API 는 ACTIVE 숙소만 검색에 노출한다.
 * 소문자 데이터가 섞여 있어 비교는 대소문자를 무시한다.
 */
public final class AccommodationStatus {

    /** 총관리자 승인 완료, 사용자 사이트 노출 */
    public static final String ACTIVE = "ACTIVE";
    /** 승인 대기 (신규 등록·승인 요청) */
    public static final String PENDING = "PENDING";
    /** 업체가 노출 중지 */
    public static final String INACTIVE = "INACTIVE";
    /** 총관리자 반려 */
    public static final String REJECTED = "REJECTED";

    private AccommodationStatus() {
    }

    public static boolean isActive(String status) {
        return status != null && ACTIVE.equals(status.trim().toUpperCase(Locale.ROOT));
    }
}
