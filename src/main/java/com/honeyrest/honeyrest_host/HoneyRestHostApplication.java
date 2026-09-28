package com.honeyrest.honeyrest_host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
// 공유 엔티티는 사용자 API 저장소의 :honeyrest-domain 모듈(com.honeyrest.domain)에서 온다.
// 호스트 전용 엔티티(ErrorLog)는 com.honeyrest.honeyrest_host.entity 에 남아 있으므로 두 패키지를 모두 스캔한다.
@EntityScan({"com.honeyrest.domain", "com.honeyrest.honeyrest_host.entity"})
public class HoneyRestHostApplication {
    public static void main(String[] args) {
        SpringApplication.run(HoneyRestHostApplication.class, args);
    }
}
