package com.honeyrest.honeyrest_host.storage;

import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 한 트랜잭션 안에서 올린/지울 파일을 DB 결과에 맞춰 정리한다. 파일 저장소는 트랜잭션에 참여하지 않으므로
 * <ul>
 *   <li>{@link #upload}: 트랜잭션이 커밋되지 않으면(예외·롤백·커밋 실패) 이번 요청에서 올린 파일을 지운다(보상).</li>
 *   <li>{@link #deleteAfterCommit}: DB 에서 뗀 파일은 커밋이 성공한 뒤에만 지운다(롤백되면 파일을 남긴다).</li>
 * </ul>
 * {@link #begin} 은 트랜잭션 동기화가 활성인 곳(@Transactional 메서드 안)에서만 부를 수 있다.
 * 파일 삭제 실패는 로그만 남긴다(본 처리 결과를 바꾸지 않는다).
 */
@Log4j2
public final class UploadCompensation implements TransactionSynchronization {

    private final FileStorage fileStorage;
    private final List<String> uploaded = new ArrayList<>();
    private final List<String> deleteOnCommit = new ArrayList<>();

    private UploadCompensation(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    public static UploadCompensation begin(FileStorage fileStorage) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("UploadCompensation 은 트랜잭션 안에서만 사용할 수 있습니다.");
        }
        UploadCompensation compensation = new UploadCompensation(fileStorage);
        TransactionSynchronizationManager.registerSynchronization(compensation);
        return compensation;
    }

    /** 파일을 올리고 URL 을 돌려준다. 트랜잭션이 커밋되지 않으면 이 파일은 지워진다. */
    public String upload(MultipartFile file, String dir) throws IOException {
        String url = fileStorage.upload(file, dir);
        uploaded.add(url);
        return url;
    }

    /** 커밋이 성공한 뒤에 지울 파일 (DB 에서 참조를 뗀 기존 이미지 등). */
    public void deleteAfterCommit(String url) {
        if (url != null && !url.isBlank()) {
            deleteOnCommit.add(url);
        }
    }

    List<String> uploadedUrls() {
        return List.copyOf(uploaded);
    }

    @Override
    public void afterCompletion(int status) {
        List<String> targets = (status == STATUS_COMMITTED) ? deleteOnCommit : uploaded;
        if (status != STATUS_COMMITTED && !targets.isEmpty()) {
            log.info("트랜잭션 미커밋 — 이번 요청에서 올린 파일 {}개를 지웁니다.", targets.size());
        }
        for (String url : targets) {
            try {
                fileStorage.delete(url);
            } catch (RuntimeException e) {
                log.warn("파일 정리 실패(수동 정리 필요): {} ({})", url, e.toString());
            }
        }
    }
}
