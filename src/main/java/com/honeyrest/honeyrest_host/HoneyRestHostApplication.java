package com.honeyrest.honeyrest_host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
// 공유 엔티티(:honeyrest-domain)와 호스트 전용 엔티티(ErrorLog)를 함께 스캔한다
@EntityScan({"com.honeyrest.domain", "com.honeyrest.honeyrest_host.entity"})
public class HoneyRestHostApplication {
    public static void main(String[] args) {
        SpringApplication.run(HoneyRestHostApplication.class, args);
    }
}
