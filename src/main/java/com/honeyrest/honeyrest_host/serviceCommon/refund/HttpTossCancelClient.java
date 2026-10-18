package com.honeyrest.honeyrest_host.serviceCommon.refund;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 실제 토스페이먼츠 결제 취소 API 호출.
 * 사용자 API HttpTossClient.cancel 과 같은 규격이다: 시크릿 키를 Basic 인증(시크릿키:빈 비밀번호)으로 보내고
 * 본문은 {@code {"cancelReason": "..."}} (금액을 보내지 않으면 전액 취소).
 * 토스가 {@code ALREADY_CANCELED_PAYMENT} 를 돌려주면 이미 취소된 것이므로 성공으로 본다(재시도 멱등성).
 */
@Log4j2
public class HttpTossCancelClient implements TossCancelClient {

    static final String TOSS_PAYMENTS_API = "https://api.tosspayments.com/v1/payments";
    static final String ALREADY_CANCELED = "ALREADY_CANCELED_PAYMENT";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String secretKey;

    public HttpTossCancelClient(RestClient restClient, ObjectMapper objectMapper, String secretKey) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.secretKey = secretKey;
    }

    @Override
    public void cancel(String paymentKey, String cancelReason) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new RefundFailedException("취소할 결제 키(paymentKey)가 없습니다.");
        }
        URI uri = UriComponentsBuilder.fromUriString(TOSS_PAYMENTS_API)
                .pathSegment(paymentKey, "cancel")
                .encode()
                .build()
                .toUri();
        String reason = (cancelReason == null || cancelReason.isBlank()) ? "호스트 예약 취소" : cancelReason;
        try {
            restClient.post()
                    .uri(uri)
                    .headers(h -> h.setBasicAuth(secretKey, "", StandardCharsets.UTF_8))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("cancelReason", reason))
                    .retrieve()
                    .toBodilessEntity();
            log.info("토스 결제 취소 완료: paymentKey={}", paymentKey);
        } catch (RestClientResponseException e) {
            String code = null;
            String message = null;
            try {
                JsonNode error = objectMapper.readTree(e.getResponseBodyAsString());
                code = error.path("code").asText(null);
                message = error.path("message").asText(null);
            } catch (Exception parseError) {
                log.debug("토스 에러 응답 파싱 실패", parseError);
            }
            if (ALREADY_CANCELED.equals(code)) {
                log.info("토스 결제가 이미 취소되어 있음(성공으로 처리): paymentKey={}", paymentKey);
                return;
            }
            log.warn("토스 결제 취소 실패: httpStatus={}, code={}, message={}", e.getStatusCode().value(), code, message);
            throw new RefundFailedException("결제 취소(환불)에 실패했습니다: " + (message != null ? message : "알 수 없는 오류"), e);
        } catch (ResourceAccessException e) {
            log.error("토스 결제 취소 통신 오류: paymentKey={}, {}", paymentKey, e.getMessage());
            throw new RefundFailedException("결제 서버와 통신하지 못해 취소(환불)하지 못했습니다. 잠시 후 다시 시도해 주세요.", e);
        }
    }
}
