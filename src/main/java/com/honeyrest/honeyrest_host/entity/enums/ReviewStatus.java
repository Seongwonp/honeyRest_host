package com.honeyrest.honeyrest_host.entity.enums;

public enum ReviewStatus {
    VISIBLE,   // 노출
    HIDDEN,    // 숨김(소프트 삭제)
    REPORTED,  // 신고(검수 대기)
    PENDING    // 임시저장/검토 전
}
