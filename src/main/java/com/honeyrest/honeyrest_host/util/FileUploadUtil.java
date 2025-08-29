package com.honeyrest.honeyrest_host.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class FileUploadUtil {

    private final Storage storage;

    /**
     * Firebase Storage 인증 및 초기화
     * - 서비스 계정 키 파일을 통해 인증
     * - 프로젝트 ID는 Firebase 콘솔에서 확인
     */
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

    /**
     * 파일 업로드
     * @param file 업로드할 MultipartFile
     * @param folder 저장할 Firebase 폴더 경로 (예: "reviews", "accommodation")
     * @return Firebase Storage에서 접근 가능한 이미지 URL
     */
    public String upload(MultipartFile file, String folder) throws Exception {
        // 파일명에 UUID 붙여서 중복 방지
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String blobName = folder + "/" + filename;

        // BlobInfo 생성: 파일 메타데이터 포함
        BlobInfo blobInfo = BlobInfo.newBuilder("honeyrest-7fb60.firebasestorage.app", blobName)
                .setContentType(file.getContentType())
                .build();

        // 파일 업로드
        storage.create(blobInfo, file.getBytes());

        // 다운로드 토큰 생성 (Firebase에서 이미지 접근 시 필요)
        String downloadToken = UUID.randomUUID().toString();
        storage.update(blobInfo.toBuilder()
                .setMetadata(Map.of("firebaseStorageDownloadTokens", downloadToken))
                .build());

        // 최종 접근 가능한 이미지 URL 반환
        return "https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/" +
               URLEncoder.encode(blobName, StandardCharsets.UTF_8) +
               "?alt=media&token=" + downloadToken;
    }

    /**
     * 이미지 삭제
     * @param folder 삭제 허용된 폴더명 (예: "reviews")
     * @param imgUrl 삭제할 이미지의 Firebase URL
     */
    public void delete(String folder, String imgUrl) {
        String blobName = extractBlobName(imgUrl);

        // 보안 체크: 지정된 폴더 안에 있는 파일만 삭제 허용
        if (blobName != null && blobName.startsWith(folder + "/")) {
            storage.delete("honeyrest-7fb60.firebasestorage.app", blobName);
        }
    }

    /**
     * Firebase 이미지 URL에서 blobName 추출
     * 예: https://.../o/reviews%2Fabc.jpg?alt=media → reviews/abc.jpg
     * @param url Firebase 이미지 URL
     * @return blobName (폴더/파일명)
     */
    private String extractBlobName(String url) {
        String decoded = URLDecoder.decode(url, StandardCharsets.UTF_8);
        int start = decoded.indexOf("/o/") + 3;
        int end = decoded.indexOf("?alt=media");
        return decoded.substring(start, end);
    }
}