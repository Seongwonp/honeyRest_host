package com.honeyrest.honeyrest_host.type;

import java.util.List;
import java.util.Locale;

/**
 * 리뷰 상태 문자열 상수 (review.status VARCHAR).
 * <p>
 * 사용자 API 는 리뷰를 {@code PUBLISHED} 로 저장하고 {@code PUBLISHED} 만 사용자 화면에 노출한다.
 * 호스트는 과거에 {@code VISIBLE} 을 노출 상태로 써서 ① 실제 사용자가 쓴 리뷰가 관리자 기본 목록(노출)에 안 보이고
 * ② 관리자가 숨김 → 노출로 되돌리면 VISIBLE 이 저장돼 사용자 사이트에서 영영 사라졌다.
 * 이제 노출 상태는 PUBLISHED 로 쓰고, 읽을 때는 과거 값 VISIBLE 도 노출로 취급한다.
 */
public final class ReviewStatus {

    /** 노출 (사용자 API 와 같은 값) */
    public static final String PUBLISHED = "PUBLISHED";
    /** 과거 호스트가 쓰던 노출 값 (읽기 호환용) */
    public static final String LEGACY_VISIBLE = "VISIBLE";
    /** 숨김 (소프트 삭제) */
    public static final String HIDDEN = "HIDDEN";

    private ReviewStatus() {
    }

    public static boolean isVisible(String status) {
        if (status == null) return false;
        String s = status.trim().toUpperCase(Locale.ROOT);
        return PUBLISHED.equals(s) || LEGACY_VISIBLE.equals(s);
    }

    /** 화면/요청의 상태 값을 저장용 값으로 정규화한다 (VISIBLE → PUBLISHED, 대문자) */
    public static String normalize(String status) {
        if (status == null || status.isBlank()) return null;
        return isVisible(status) ? PUBLISHED : status.trim().toUpperCase(Locale.ROOT);
    }

    /** 상태 필터를 조회용 값 목록으로 펼친다 (노출 = PUBLISHED + 과거 VISIBLE) */
    public static List<String> expand(String status) {
        String normalized = normalize(status);
        if (normalized == null) return List.of();
        return PUBLISHED.equals(normalized) ? List.of(PUBLISHED, LEGACY_VISIBLE) : List.of(normalized);
    }
}
