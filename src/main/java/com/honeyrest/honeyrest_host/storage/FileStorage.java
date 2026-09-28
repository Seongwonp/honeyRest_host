package com.honeyrest.honeyrest_host.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 업로드 파일 저장소 추상화.
 * <p>
 * 구현체는 {@code app.storage.type} 프로퍼티로 선택한다.
 * <ul>
 *     <li>{@code local} (기본값) : {@link LocalFileStorage} - 서버 로컬 디스크에 저장하고 {@code /uploads/**} 로 서빙</li>
 *     <li>{@code firebase} : {@link FirebaseFileStorage} - Firebase Storage 버킷에 저장</li>
 * </ul>
 */
public interface FileStorage {

    /**
     * 파일을 저장하고 브라우저에서 접근 가능한 URL을 반환한다.
     *
     * @param file 업로드할 파일
     * @param dir  저장 디렉터리(논리 경로, 예: "reviews", "accommodations/3/images")
     * @return 접근 가능한 이미지 URL
     */
    String upload(MultipartFile file, String dir) throws IOException;

    /**
     * URL이 가리키는 파일을 삭제한다. 이 저장소가 만든 URL이 아니면 아무 것도 하지 않는다.
     */
    void delete(String url);

    /**
     * URL에서 저장소 내부 키(디렉터리/파일명)를 추출한다. 이 저장소의 URL이 아니면 null.
     */
    String extractKey(String url);

    /**
     * 지정한 디렉터리 하위의 파일일 때만 삭제한다(다른 폴더 파일 삭제 방지용 안전장치).
     */
    default void delete(String dir, String url) {
        String key = extractKey(url);
        if (key != null && key.startsWith(dir + "/")) {
            delete(url);
        }
    }
}
