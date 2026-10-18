package com.honeyrest.honeyrest_host.serviceCommon.refund;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * 토스 결제 취소 클라이언트 선택.
 * {@code toss.secret-key}(환경변수 TOSS_SECRET_KEY)가 비어 있거나 screenshot 프로필이면 {@link NoOpTossCancelClient},
 * 그 외에는 {@link HttpTossCancelClient} 를 등록한다. 시크릿 키는 사용자 API 와 같은 토스 상점 키를 쓴다.
 */
@Log4j2
@Configuration
public class TossCancelClientConfig {

    @Bean
    public TossCancelClient tossCancelClient(@Value("${toss.secret-key:}") String secretKey,
                                             Environment environment,
                                             RestClient.Builder restClientBuilder,
                                             ObjectMapper objectMapper) {
        if (secretKey == null || secretKey.isBlank() || environment.acceptsProfiles(Profiles.of("screenshot"))) {
            log.warn("토스 결제 취소 클라이언트: No-op (toss.secret-key 미설정 또는 screenshot 프로필). 예약 취소 시 PG 환불은 호출되지 않습니다.");
            return new NoOpTossCancelClient();
        }
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        RestClient restClient = restClientBuilder.clone().requestFactory(requestFactory).build();
        return new HttpTossCancelClient(restClient, objectMapper, secretKey);
    }
}
