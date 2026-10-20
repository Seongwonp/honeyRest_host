package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationCategoryRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationImageRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OCompanyRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORegionRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomRepository;
import com.honeyrest.honeyrest_host.storage.FileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 숙소 승인/거절(SUPER_ADMIN)은 PENDING 상태에서만 가능해야 한다.
 */
@ExtendWith(MockitoExtension.class)
class OAccommodationServiceImplTest {

    @Mock private OAccommodationRepository accommodationRepository;
    @Mock private OCompanyRepository companyRepository;
    @Mock private ORegionRepository regionRepository;
    @Mock private OAccommodationCategoryRepository accommodationCategoryRepository;
    @Mock private OAccommodationImageRepository accommodationImageRepository;
    @Mock private ObjectMapper objectMapper;
    @Mock private FileStorage fileStorage;
    @Mock private ORoomRepository roomRepository;
    @Mock private SearchCacheInvalidator searchCacheInvalidator;

    private OAccommodationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OAccommodationServiceImpl(
                accommodationRepository, companyRepository, regionRepository,
                accommodationCategoryRepository, accommodationImageRepository,
                objectMapper, fileStorage, roomRepository, searchCacheInvalidator);
    }

    @Test
    void PENDING_상태의_숙소는_승인된다() {
        when(accommodationRepository.updateStatusIfCurrent(1L, "PENDING", "ACTIVE")).thenReturn(1);

        assertThatCode(() -> service.approve(1L)).doesNotThrowAnyException();
        verify(searchCacheInvalidator).bumpAfterCommit(); // 승인 → 검색에 새로 노출
    }

    @Test
    void PENDING이_아니면_승인이_거부된다() {
        when(accommodationRepository.updateStatusIfCurrent(1L, "PENDING", "ACTIVE")).thenReturn(0);

        assertThatThrownBy(() -> service.approve(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("승인 대기");
        verify(searchCacheInvalidator, never()).bumpAfterCommit();
    }

    @Test
    void PENDING이_아니면_거절도_거부된다() {
        when(accommodationRepository.updateStatusIfCurrent(1L, "PENDING", "REJECTED")).thenReturn(0);

        assertThatThrownBy(() -> service.reject(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("승인 대기");
    }
}
