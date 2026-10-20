package com.honeyrest.honeyrest_host.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final NotificationInterceptor notificationInterceptor;

    // 로컬 스토리지 모드(app.storage.type=local)에서 업로드된 파일이 저장되는 디렉터리
    @Value("${app.storage.local.dir:./uploads}")
    private String localStorageDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(notificationInterceptor)
                .addPathPatterns("/admin/**", "/owner/**")
                .excludePathPatterns("/assets/**", "/css/**", "/js/**", "/error/**", "/uploads/**");
    }

    /** 로컬 저장소에 업로드된 파일을 /uploads/** 경로로 서빙한다. */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(localStorageDir).toAbsolutePath().normalize().toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
