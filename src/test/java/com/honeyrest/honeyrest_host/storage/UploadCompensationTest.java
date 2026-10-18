package com.honeyrest.honeyrest_host.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UploadCompensationTest {

    private FileStorage storage;

    @BeforeEach
    void setUp() throws Exception {
        storage = mock(FileStorage.class);
        when(storage.upload(any(), eq("a"))).thenReturn("/uploads/a/1.jpg", "/uploads/a/2.jpg");
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDown() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    private static MockMultipartFile file() {
        return new MockMultipartFile("f", "x.jpg", "image/jpeg", new byte[]{1});
    }

    private void complete(int status) {
        TransactionSynchronizationManager.getSynchronizations().forEach(s -> s.afterCompletion(status));
    }

    @Test
    void 롤백되면_이번_요청에서_올린_파일을_지우고_예약된_삭제는_하지_않는다() throws Exception {
        UploadCompensation c = UploadCompensation.begin(storage);
        c.upload(file(), "a");
        c.upload(file(), "a");
        c.deleteAfterCommit("/uploads/a/old.jpg");

        complete(TransactionSynchronization.STATUS_ROLLED_BACK);

        verify(storage).delete("/uploads/a/1.jpg");
        verify(storage).delete("/uploads/a/2.jpg");
        verify(storage, never()).delete("/uploads/a/old.jpg");
    }

    @Test
    void 커밋되면_올린_파일은_남기고_뗀_파일만_지운다() throws Exception {
        UploadCompensation c = UploadCompensation.begin(storage);
        c.upload(file(), "a");
        c.deleteAfterCommit("/uploads/a/old.jpg");

        complete(TransactionSynchronization.STATUS_COMMITTED);

        verify(storage).delete("/uploads/a/old.jpg");
        verify(storage, never()).delete("/uploads/a/1.jpg");
        assertThat(c.uploadedUrls()).containsExactly("/uploads/a/1.jpg");
    }

    @Test
    void 트랜잭션_밖에서는_시작할_수_없다() {
        TransactionSynchronizationManager.clearSynchronization();
        assertThatThrownBy(() -> UploadCompensation.begin(storage)).isInstanceOf(IllegalStateException.class);
        TransactionSynchronizationManager.initSynchronization(); // tearDown 대칭
        verify(storage, never()).delete(anyString());
    }
}
