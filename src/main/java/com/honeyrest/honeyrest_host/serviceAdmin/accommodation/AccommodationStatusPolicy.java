package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;

import java.util.Locale;
import java.util.Set;

/**
 * 회사 관리자(COMPANY_ADMIN)가 숙소 등록·수정 화면에서 바꿀 수 있는 숙소 상태 규칙.
 *
 * <p>과거에는 수정 폼의 status 값을 그대로 저장해 회사 관리자가 ACTIVE 를 골라
 * 총관리자(SUPER_ADMIN) 승인 없이 숙소를 바로 노출시킬 수 있었다.
 * ACTIVE(노출)·REJECTED(거절)는 총관리자 승인 흐름
 * ({@code OAccommodationService.approve/reject}, PENDING 에서만 전환)에서만 만들어진다.</p>
 *
 * <ul>
 *   <li>PENDING  — 승인 요청(재요청 포함). 어떤 상태에서든 가능</li>
 *   <li>INACTIVE — 운영 중지. 어떤 상태에서든 가능 (다시 노출하려면 PENDING 으로 재승인 요청)</li>
 *   <li>현재 상태 그대로 제출 — 변경 없음으로 보고 허용 (ACTIVE 숙소의 일반 정보 수정)</li>
 *   <li>그 외(ACTIVE, REJECTED, 알 수 없는 값)로의 변경 — 거부</li>
 * </ul>
 */
public final class AccommodationStatusPolicy {

    /** 회사 관리자가 직접 선택할 수 있는 상태 */
    public static final Set<String> COMPANY_ADMIN_SELECTABLE = Set.of("PENDING", "INACTIVE");

    /** 회사 관리자가 신규 등록할 때 강제되는 상태 */
    public static final String INITIAL_STATUS = "PENDING";

    private AccommodationStatusPolicy() {
    }

    /**
     * 회사 관리자의 상태 변경 요청을 검증하고 실제로 저장할 값을 돌려준다.
     *
     * @param currentStatus   현재 저장된 상태
     * @param requestedStatus 폼에서 제출된 상태 (null/빈 값이면 변경 없음)
     * @return 저장할 상태(대문자), 변경이 없으면 null
     * @throws IllegalArgumentException 허용되지 않은 상태로 바꾸려는 경우
     */
    public static String resolveCompanyAdminStatus(String currentStatus, String requestedStatus) {
        if (requestedStatus == null || requestedStatus.isBlank()) {
            return null;
        }
        String requested = requestedStatus.trim().toUpperCase(Locale.ROOT);
        String current = currentStatus == null ? null : currentStatus.trim().toUpperCase(Locale.ROOT);

        if (requested.equals(current)) {
            return null; // 상태 변경 없음
        }
        if (!COMPANY_ADMIN_SELECTABLE.contains(requested)) {
            throw new IllegalArgumentException(
                    "회사 관리자는 숙소 상태를 PENDING(승인 요청) 또는 INACTIVE(운영 중지)로만 변경할 수 있습니다. "
                            + "ACTIVE 는 총관리자 승인으로만 전환됩니다.");
        }
        return requested;
    }
}
