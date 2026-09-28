package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;

import com.honeyrest.domain.entity.Accommodation;
import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationUpdateRequestDTO;
import com.honeyrest.honeyrest_host.repositoryAdmin.CancellationPolicyRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.CompanyRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RegionRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationCategoryRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationImageRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationTagMapRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationTagRepository;
import com.honeyrest.honeyrest_host.serviceAdmin.CancellationPolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 승인 우회 차단 회귀 테스트: 회사 관리자가 숙소 수정 폼으로 ACTIVE 를 골라
 * 총관리자 승인 없이 숙소를 노출시킬 수 없어야 한다.
 */
class AccommodationStatusGuardTest {

    @Nested
    @DisplayName("상태 정책")
    class Policy {

        @ParameterizedTest(name = "{0} → {1} 은 거부")
        @CsvSource({
                "PENDING, ACTIVE",
                "INACTIVE, ACTIVE",
                "REJECTED, ACTIVE",
                "PENDING, active",
                "PENDING, REJECTED",
                "ACTIVE, APPROVED",
                "PENDING, DISABLED"
        })
        void 허용되지_않은_상태로의_변경은_거부된다(String current, String requested) {
            assertThatThrownBy(() -> AccommodationStatusPolicy.resolveCompanyAdminStatus(current, requested))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("총관리자 승인");
        }

        @ParameterizedTest(name = "{0} → {1} 은 {2} 로 저장")
        @CsvSource({
                "ACTIVE, INACTIVE, INACTIVE",
                "ACTIVE, PENDING, PENDING",
                "REJECTED, PENDING, PENDING",
                "INACTIVE, pending, PENDING",
                "PENDING, INACTIVE, INACTIVE"
        })
        void 승인요청과_운영중지는_허용된다(String current, String requested, String expected) {
            assertThat(AccommodationStatusPolicy.resolveCompanyAdminStatus(current, requested)).isEqualTo(expected);
        }

        @Test
        void 현재_상태_그대로이거나_빈값이면_변경하지_않는다() {
            // ACTIVE 숙소의 일반 정보 수정은 status=ACTIVE 가 그대로 제출된다
            assertThat(AccommodationStatusPolicy.resolveCompanyAdminStatus("ACTIVE", "ACTIVE")).isNull();
            assertThat(AccommodationStatusPolicy.resolveCompanyAdminStatus("ACTIVE", "")).isNull();
            assertThat(AccommodationStatusPolicy.resolveCompanyAdminStatus("PENDING", null)).isNull();
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("서비스 update")
    class ServiceUpdate {

        @Mock AccommodationRepository accommodationRepository;
        @Mock CompanyRepository companyRepository;
        @Mock RegionRepository regionRepository;
        @Mock AccommodationCategoryRepository accommodationCategoryRepository;
        @Mock AccommodationTagRepository accommodationTagRepository;
        @Mock AccommodationImageRepository accommodationImageRepository;
        @Mock AccommodationTagMapRepository accommodationTagMapRepository;
        @Mock AccommodationImageService accommodationImageService;
        @Mock CancellationPolicyRepository cancellationPolicyRepository;
        @Mock ReservationRepository reservationRepository;
        @Mock SearchCacheInvalidator searchCacheInvalidator;
        @Mock CancellationPolicyService cancellationPolicyService;
        @Mock AccommodationTagService accommodationTagService;
        @Mock RoomRepository roomRepository;

        @InjectMocks AccommodationServiceImpl service;

        private void givenAccommodation(String status) {
            Accommodation acc = Accommodation.builder().accommodationId(1L).name("테스트 숙소").status(status).build();
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(acc));
        }

        @Test
        void PENDING_숙소를_ACTIVE로_수정하면_거부되고_저장하지_않는다() {
            givenAccommodation("PENDING");
            AccommodationUpdateRequestDTO form = AccommodationUpdateRequestDTO.builder().name("새 이름").status("ACTIVE").build();

            assertThatThrownBy(() -> service.update(1L, form))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(accommodationRepository, never()).patchUpdateScalars(
                    anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
            verify(searchCacheInvalidator, never()).bumpAfterCommit();
        }

        @Test
        void ACTIVE_숙소의_일반_수정은_상태를_건드리지_않는다() {
            givenAccommodation("ACTIVE");
            when(accommodationRepository.patchUpdateScalars(
                    eq(1L), any(), any(), any(), any(), any(), any(), any(), any(), any(), isNull(), any()))
                    .thenReturn(1);
            AccommodationUpdateRequestDTO form = AccommodationUpdateRequestDTO.builder().name("새 이름").status("ACTIVE").build();

            service.update(1L, form);

            // status 인자로 null(변경 없음)이 전달돼야 한다
            verify(accommodationRepository).patchUpdateScalars(
                    eq(1L), eq("새 이름"), any(), any(), any(), any(), any(), any(), any(), any(), isNull(), any());
        }

        @Test
        void ACTIVE_숙소를_운영중지하면_INACTIVE로_저장된다() {
            givenAccommodation("ACTIVE");
            when(accommodationRepository.patchUpdateScalars(
                    eq(1L), any(), any(), any(), any(), any(), any(), any(), any(), any(), eq("INACTIVE"), any()))
                    .thenReturn(1);
            AccommodationUpdateRequestDTO form = AccommodationUpdateRequestDTO.builder().status("INACTIVE").build();

            service.update(1L, form);

            verify(searchCacheInvalidator).bumpAfterCommit(); // 노출 중단 → 검색 캐시 무효화
        }
    }
}
