package com.honeyrest.honeyrest_host.serviceOwner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationCategoryRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationImageRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OCompanyRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORegionRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomRepository;
import com.honeyrest.honeyrest_host.utilAdmin.FileUploadUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * P1-1/P1-2 회귀 테스트: 숙소 승인/거절은 PENDING 상태에서만 가능해야 한다.
 * (회사 관리자의 자가 승인 차단은 changeStatus 쪽에서 별도로 막혀 있고, 여기는
 * SUPER_ADMIN 전용 승인/거절 자체의 상태 전이 가드를 검증한다.)
 */
@ExtendWith(MockitoExtension.class)
class OAccommodationServiceImplTest {

    @Mock private OAccommodationRepository accommodationRepository;
    @Mock private OCompanyRepository companyRepository;
    @Mock private ORegionRepository regionRepository;
    @Mock private OAccommodationCategoryRepository accommodationCategoryRepository;
    @Mock private OAccommodationImageRepository accommodationImageRepository;
    @Mock private ObjectMapper objectMapper;
    @Mock private FileUploadUtil fileUploadUtil;
    @Mock private ORoomRepository roomRepository;

    private OAccommodationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OAccommodationServiceImpl(
                accommodationRepository, companyRepository, regionRepository,
                accommodationCategoryRepository, accommodationImageRepository,
                objectMapper, fileUploadUtil, roomRepository);
    }

    @Test
    void PENDING_상태의_숙소는_승인된다() {
        when(accommodationRepository.updateStatusIfCurrent(1L, "PENDING", "ACTIVE")).thenReturn(1);

        assertThatCode(() -> service.approve(1L)).doesNotThrowAnyException();
    }

    @Test
    void PENDING이_아니면_승인이_거부된다() {
        when(accommodationRepository.updateStatusIfCurrent(1L, "PENDING", "ACTIVE")).thenReturn(0);

        assertThatThrownBy(() -> service.approve(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("승인 대기");
    }

    @Test
    void PENDING이_아니면_거절도_거부된다() {
        when(accommodationRepository.updateStatusIfCurrent(1L, "PENDING", "REJECTED")).thenReturn(0);

        assertThatThrownBy(() -> service.reject(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("승인 대기");
    }
}
