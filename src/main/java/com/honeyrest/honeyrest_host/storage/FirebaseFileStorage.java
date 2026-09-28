package com.honeyrest.honeyrest_host.storage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * Firebase Storage 저장소 ({@code app.storage.type=firebase} 일 때만 활성화).
 * <p>
 * 기존 FileUploadUtil 의 업로드/삭제 로직을 옮겨 온 구현이다.
 * 인증 정보는 FirebaseConfig 가 초기화한 {@link FirebaseApp} 을 재사용하므로
 * 서비스 계정 키를 이 클래스에서 다시 읽지 않는다.
 */
@Log4j2
@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "firebase")
public class FirebaseFileStorage implements FileStorage {

    private static final String DOWNLOAD_URL_PREFIX = "https://firebasestorage.googleapis.com/v0/b/";

    private final Storage storage;
    private final String bucketName;

    public FirebaseFileStorage(FirebaseApp firebaseApp) {
        Bucket bucket = StorageClient.getInstance(firebaseApp).bucket();
        this.storage = bucket.getStorage();
        this.bucketName = bucket.getName();
    }

    @Override
    public String upload(MultipartFile file, String dir) throws IOException {
        // 파일명에 UUID 붙여서 중복 방지
        String blobName = dir + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        // 다운로드 토큰 (Firebase 공개 URL 접근 시 필요) 을 메타데이터로 함께 저장
        String downloadToken = UUID.randomUUID().toString();
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName)
                .setContentType(file.getContentType())
                .setMetadata(Map.of("firebaseStorageDownloadTokens", downloadToken))
                .build();
        storage.create(blobInfo, file.getBytes());

        return DOWNLOAD_URL_PREFIX + bucketName + "/o/"
                + URLEncoder.encode(blobName, StandardCharsets.UTF_8)
                + "?alt=media&token=" + downloadToken;
    }

    @Override
    public void delete(String url) {
        String blobName = extractKey(url);
        if (blobName == null) {
            return;
        }
        boolean deleted = storage.delete(bucketName, blobName);
        if (!deleted) {
            log.debug("Firebase 파일이 이미 없거나 삭제되지 않았습니다: {}", blobName);
        }
    }

    /**
     * Firebase 이미지 URL에서 blobName 추출
     * 예: https://.../o/reviews%2Fabc.jpg?alt=media → reviews/abc.jpg
     */
    @Override
    public String extractKey(String url) {
        if (url == null || !url.startsWith(DOWNLOAD_URL_PREFIX)) {
            return null;
        }
        int start = url.indexOf("/o/");
        if (start < 0) {
            return null;
        }
        int end = url.indexOf('?', start);
        String encoded = end < 0 ? url.substring(start + 3) : url.substring(start + 3, end);
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }
}
