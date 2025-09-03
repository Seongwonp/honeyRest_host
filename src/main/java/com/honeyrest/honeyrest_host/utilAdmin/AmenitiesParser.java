package com.honeyrest.honeyrest_host.utilAdmin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class AmenitiesParser {

    private static final ObjectMapper om = new ObjectMapper();

    private AmenitiesParser() {}
    /** JSON 배열 문자열/CSV/멀티라인 어떤 형식이든 → List<String> */
    public static List<String> toList(String anyFormat) {
        if (anyFormat == null || anyFormat.isBlank()) return Collections.emptyList();

        // 1) JSON 배열 우선 시도
        try {
            List<String> json = om.readValue(anyFormat, new TypeReference<List<String>>() {});
            return sanitize(json);
        } catch (Exception ignore) { }

        // 2) CSV 시도
        if (anyFormat.contains(",")) {
            return sanitize(Arrays.asList(anyFormat.split(",")));
        }

        // 3) 멀티라인 시도
        return fromMultiline(anyFormat);
    }

    /** 멀티라인 문자열 → List<String> */
    public static List<String> fromMultiline(String multiline) {
        if (multiline == null || multiline.isBlank()) return Collections.emptyList();
        return Arrays.stream(multiline.split("\\R"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    /** 저장용: 어떤 입력이든(JSON/CSV/멀티라인) → JSON 배열 문자열 */
    public static String normalizeToJson(String anyFormat) {
        List<String> list = toList(anyFormat);
        return toJson(list);
    }

    /** List<String> → JSON 배열 문자열 */
    public static String toJson(List<String> items) {
        List<String> cleaned = sanitize(items);
        try {
            return om.writeValueAsString(cleaned);
        } catch (Exception e) {
            return "[]";
        }
    }

    /** JSON/CSV/멀티라인 → textarea 표시용 줄바꿈 문자열 */
    public static String toMultiline(String anyFormat) {
        List<String> list = toList(anyFormat);
        return String.join("\n", list);
    }

    /** List<String> → textarea 표시용 줄바꿈 문자열 */
    public static String toMultiline(List<String> items) {
        List<String> cleaned = sanitize(items);
        return String.join("\n", cleaned);
    }

    private static List<String> sanitize(List<String> raw) {
        if (raw == null) return Collections.emptyList();
        return raw.stream()
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> !s.isBlank())
                .distinct()  // 중복 제거 + 입력 순서 유지
                .collect(Collectors.toList());
    }
}