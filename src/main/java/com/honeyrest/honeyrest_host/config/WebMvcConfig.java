package com.honeyrest.honeyrest_host.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final NotificationInterceptor notificationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(notificationInterceptor)
                .addPathPatterns("/admin/**", "/owner/**")
                .excludePathPatterns("/assets/**", "/css/**", "/js/**", "/error/**");
    }
}
