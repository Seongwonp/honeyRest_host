package com.honeyrest.honeyrest_host.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws Exception {
        InputStream serviceAccount = getClass().getClassLoader()
                .getResourceAsStream("honeyrest-7fb60-firebase-adminsdk-fbsvc-17f8ee9da5.json");

        if (serviceAccount == null) {
            throw new IllegalStateException("Firebase 서비스 계정 키 파일을 찾을 수 없습니다.");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("honeyrest-7fb60.firebasestorage.app")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}