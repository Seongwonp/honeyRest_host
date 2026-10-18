package com.honeyrest.honeyrest_host.serviceCommon.refund;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * 토스 결제 취소 HTTP 호출 규격 (사용자 API HttpTossClient.cancel 과 같은 URL·인증·본문).
 */
class HttpTossCancelClientTest {

    private MockRestServiceServer server;
    private HttpTossCancelClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new HttpTossCancelClient(builder.build(), new ObjectMapper(), "test_sk_123");
    }

    @Test
    void 결제키_경로와_Basic_인증과_취소사유로_POST_한다() {
        String basic = "Basic " + Base64.getEncoder().encodeToString("test_sk_123:".getBytes(StandardCharsets.UTF_8));
        server.expect(requestTo("https://api.tosspayments.com/v1/payments/pk_abc/cancel"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, basic))
                .andExpect(content().json("{\"cancelReason\":\"고객 요청\"}"))
                .andRespond(withSuccess("{\"status\":\"CANCELED\"}", MediaType.APPLICATION_JSON));

        client.cancel("pk_abc", "고객 요청");
        server.verify();
    }

    @Test
    void 이미_취소된_결제는_성공으로_본다() {
        server.expect(requestTo("https://api.tosspayments.com/v1/payments/pk_abc/cancel"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"code\":\"ALREADY_CANCELED_PAYMENT\",\"message\":\"이미 취소된 결제 입니다.\"}"));

        assertThatCode(() -> client.cancel("pk_abc", "중복")).doesNotThrowAnyException();
    }

    @Test
    void 그_외_오류는_RefundFailedException() {
        server.expect(requestTo("https://api.tosspayments.com/v1/payments/pk_abc/cancel"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"code\":\"NOT_CANCELABLE_PAYMENT\",\"message\":\"취소 할 수 없는 결제 입니다.\"}"));

        assertThatThrownBy(() -> client.cancel("pk_abc", "x"))
                .isInstanceOf(RefundFailedException.class)
                .hasMessageContaining("취소 할 수 없는 결제");
    }
}
