package com.honeyrest.honeyrest_host.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /*
    목적: JSON ↔ Java 객체 변환. 즉, 문자열(JSON)과 JsonNode, DTO, Map 같은 JSON 데이터 처리에 특화.
    지금 ObjectMapper가 필요한 이유 : amenities 필드 떄문임.
    ! 알고 있기 !
     */

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();

    }
}
