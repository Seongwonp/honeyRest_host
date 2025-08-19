package com.honeyrest.honeyrest_host.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi restApi() {
        return GroupedOpenApi.builder()
                .group("REST_API")
                .pathsToMatch("/api/**")        // ✅ 표준 패턴
                .build();
    }

    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .group("COMMON_API")            // ✅ 공백 없이
                .pathsToMatch("/**")            // ✅ 모든 비-API 경로(관리자 화면 등)
                .pathsToExclude(
                        "/api/**",              // ✅ REST 그룹과 중복 방지
                        "/v3/**",               // swagger 자체 경로 제외
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico",
                        "/error"
                )
                .build();
    }
}