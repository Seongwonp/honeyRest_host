package com.honeyrest.honeyrest_host.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Firebase 초기화 설정.
 * <p>
 * {@code app.storage.type=firebase} 일 때만 로드된다. 기본값(local)에서는 이 설정이 비활성화되므로
 * 서비스 계정 키 파일이 없어도 애플리케이션이 정상 기동한다.
 */
@Configuration
@ConditionalOnProperty(name = "app.storage.type", havingValue = "firebase")
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(
            @Value("${app.firebase.credentials:classpath:honeyrest-7fb60-firebase-adminsdk-fbsvc-70bafb1ad4.json}") Resource credentials,
            @Value("${app.firebase.bucket:honeyrest-7fb60.firebasestorage.app}") String bucket
    ) throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }
        if (!credentials.exists()) {
            throw new IllegalStateException("Firebase 서비스 계정 키 파일을 찾을 수 없습니다: " + credentials.getDescription()
                    + " (app.firebase.credentials 로 경로를 지정하거나 app.storage.type=local 을 사용하세요)");
        }
        try (InputStream serviceAccount = credentials.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(bucket)
                    .build();
            return FirebaseApp.initializeApp(options);
        }
    }
}
