package com.honeyrest.honeyrest_host.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class FileUploadUtil {

    private final Storage storage;

    public FileUploadUtil() throws Exception {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream("src/main/resources/honeyrest-7fb60-firebase-adminsdk-fbsvc-17f8ee9da5.json")) // 🔑 경로 확인
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId("honeyrest-7fb60")
                .build()
                .getService();
    }

    public String upload(MultipartFile file, String folder) throws Exception {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String blobName = folder + "/" + filename;

        BlobInfo blobInfo = BlobInfo.newBuilder("honeyrest-7fb60.firebasestorage.app", blobName)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        String downloadToken = UUID.randomUUID().toString();
        storage.update(blobInfo.toBuilder()
                .setMetadata(Map.of("firebaseStorageDownloadTokens", downloadToken))
                .build());

        return "https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/" +
                URLEncoder.encode(blobName, StandardCharsets.UTF_8) +
                "?alt=media&token=" + downloadToken;
    }
}