package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;

import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.storage.FileStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 숙소 등록 흐름이 한 트랜잭션이고, 실패하면 그 요청에서 올린 파일을 지우는지(보상) 검증한다.
 * 테스트 메서드에 @Transactional 을 두지 않는다: 서비스 자신의 트랜잭션이 롤백되어야 보상이 실행된다.
 */
@SpringBootTest
@ActiveProfiles("test")
class AccommodationRegistrationServiceTest {

    @Autowired private AccommodationRegistrationService registrationService;
    @MockitoBean private FileStorage fileStorage;

    @Test
    void 숙소_저장이_실패하면_이미_올린_썸네일과_서브이미지를_지운다() throws Exception {
        when(fileStorage.upload(any(), eq("accommodations"))).thenReturn("/uploads/accommodations/thumb.jpg");

        AccommodationCreateRequestDTO form = new AccommodationCreateRequestDTO();
        form.setFile(new MockMultipartFile("file", "thumb.jpg", "image/jpeg", new byte[]{1, 2}));
        // 이름·주소·카테고리·지역 누락 → AccommodationService.create 가 IllegalArgumentException

        assertThatThrownBy(() -> registrationService.register(987_654, form,
                List.of(new MockMultipartFile("subImages", "s.jpg", "image/jpeg", new byte[]{3}))))
                .isInstanceOf(IllegalArgumentException.class);

        verify(fileStorage).delete("/uploads/accommodations/thumb.jpg");
        // 서브 이미지는 숙소 저장 뒤 단계라 올라가지 않았다
        verify(fileStorage, never()).upload(any(), eq("accommodations/null/images"));
        verify(fileStorage, never()).delete(eq("/uploads/accommodations/other.jpg"));
    }

    @Test
    void 파일이_없으면_업로드도_보상도_없다() throws Exception {
        AccommodationCreateRequestDTO form = new AccommodationCreateRequestDTO();

        assertThatThrownBy(() -> registrationService.register(987_654, form, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(fileStorage, never()).upload(any(), anyString());
        verify(fileStorage, never()).delete(anyString());
    }
}
