package com.honeyrest.honeyrest_host.service;


import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Log4j2
@Service
@RequiredArgsConstructor
public class MapService {
    @Value("${naver.maps.client-id}")
    private String clientId;

    @Value("${naver.maps.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AccommodationCreateRequestDTO getCoordinates(String address) {
        String url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query="
                + UriUtils.encode(address, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("지오코딩 요청 실패: status={}, body=null? {}", response.getStatusCode(), response.getBody() == null);
            throw new RuntimeException("지오코딩 요청 실패");
        }
        JsonNode root;
        try {
            root = objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            log.error("지오코딩 응답 파싱 실패: body={}", response.getBody(), e);
            throw new RuntimeException("지오코딩 응답 파싱 실패", e);
        }
        JsonNode addresses = root.path("addresses");
        if (!addresses.isArray() || addresses.isEmpty()) {
            throw new RuntimeException("주소 변환 실패: " + address);
        }
        JsonNode first = addresses.get(0);
        double x = first.path("x").asDouble(); // 경도 (문자열이어도 숫자로 파싱됨)
        double y = first.path("y").asDouble(); // 위도

        return AccommodationCreateRequestDTO.builder()
                .address(address) // 검색한 주소
                .latitude(BigDecimal.valueOf(y))
                .longitude(BigDecimal.valueOf(x))
                .build();
    }
}