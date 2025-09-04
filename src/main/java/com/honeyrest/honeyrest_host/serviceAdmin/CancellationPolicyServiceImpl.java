package com.honeyrest.honeyrest_host.serviceAdmin;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.entity.CancellationPolicy;
import com.honeyrest.honeyrest_host.repositoryAdmin.CancellationPolicyRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CancellationPolicyServiceImpl implements CancellationPolicyService {

    private final CancellationPolicyRepository cancellationPolicyRepository;
    private final AccommodationRepository accommodationRepository;
    private final ObjectMapper objectMapper;



    /* detail - 리스트 */
    private List<String> parseDetail(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        String s = raw.trim();
        try {
            if ((s.startsWith("[") && s.endsWith("]")) || (s.startsWith("{") && s.endsWith("}"))) {
                JsonNode node = objectMapper.readTree(s);
                List<String> out = new ArrayList<>();
                if (node.isArray()) {
                    node.forEach(n -> out.add(n.asText()));
                } else if (node.has("items") && node.get("items").isArray()) {
                    node.get("items").forEach(n -> out.add(n.asText()));
                }
                return out;
            }
        } catch (Exception ignored) { /* JSON 아님 → 아래로 */ }

        // 개행 또는 쉼표 분리
        String[] parts = s.split("\\r?\\n|,");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }


    @Override
    public String getMultilineByAccommodationId(Long accommodationId) {
        return cancellationPolicyRepository
                .findTop1ByAccommodation_AccommodationIdOrderByPolicyIdDesc(accommodationId) // 최신 1건
                .map(cp -> String.join("\n", parseJsonArray(cp.getDetail())))
                .orElse("");
    }

    @Transactional
    @Override
    public void saveOrUpdate(Long accommodationId, String multiline)  {
        // 비었으면 삭제
        if (multiline == null || multiline.isBlank()) {
            cancellationPolicyRepository.deleteByAccommodation_AccommodationId(accommodationId);
            return;
        }

        // 멀티라인 -> 리스트 -> JSON
        List<String> items = Arrays.stream(multiline.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        String json;
        try { json = objectMapper.writeValueAsString(items); }
        catch (Exception e) { json = "[]"; }

        // 이미 있으면 UPDATE, 없으면 INSERT
        var existing = cancellationPolicyRepository
                .findTop1ByAccommodation_AccommodationIdOrderByPolicyIdDesc(accommodationId);

        if (existing.isPresent()) {
            cancellationPolicyRepository.updateDetailByAccId(accommodationId, json, "기본 환불 정책");
        } else {
            CancellationPolicy entity = CancellationPolicy.builder()
                    .accommodation(accommodationRepository.getReferenceById(accommodationId))
                    .policyName("기본 환불 정책")
                    .detail(json)
                    .build();
            cancellationPolicyRepository.save(entity);
        }
    }

    // ===== helper =====
    private List<String> parseJsonArray(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        String s = raw.trim();
        try {
            if ((s.startsWith("[") && s.endsWith("]")) || (s.startsWith("{") && s.endsWith("}"))) {
                JsonNode node = objectMapper.readTree(s);
                List<String> out = new ArrayList<>();
                if (node.isArray()) {
                    node.forEach(n -> out.add(n.asText()));
                } else if (node.has("items") && node.get("items").isArray()) {
                    node.get("items").forEach(n -> out.add(n.asText()));
                }
                if (!out.isEmpty()) return out;
            }
        } catch (Exception ignored) { /* JSON 아닐 때 아래로 */ }

        // 개행/쉼표 분리
        return Arrays.stream(s.split("\\r?\\n|,"))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .toList();
    }
}

