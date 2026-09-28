package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.cache.SearchCacheInvalidator;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
import com.honeyrest.domain.entity.Reservation;
import com.honeyrest.domain.type.ReservationStatus;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OReservationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationInventoryGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 오너 예약 목록: 상태 필터가 리포지토리 쿼리로 전달되고 totalCount 가 필터된 count 를 쓰는지 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class OReservationServicePageTest {

    @Mock private OReservationRepository reservationRepository;
    @Mock private ORoomRepository roomRepository;
    @Mock private OUserRepository userRepository;
    @Mock private OAccommodationRepository accommodationRepository;
    @Mock private ReservationInventoryGuard guard;
    @Mock private SearchCacheInvalidator searchCacheInvalidator;

    private OReservationService service;
    private PageRequestDTO pageRequest;

    @BeforeEach
    void setUp() {
        service = new OReservationService(reservationRepository, roomRepository, userRepository,
                accommodationRepository, guard, searchCacheInvalidator);
        pageRequest = new PageRequestDTO();
    }

    private Page<Reservation> emptyPageWithTotal(long total) {
        return new PageImpl<>(List.of(), Pageable.ofSize(10), total);
    }

    @Test
    void 업체별_활성목록은_취소상태를_쿼리에서_제외하고_필터된_total을_쓴다() {
        when(reservationRepository.findPageByCompanyExcludingStatuses(eq(3),
                eq(List.of(ReservationStatus.CANCELLED, ReservationStatus.CANCEL_REQUEST)), any()))
                .thenReturn(emptyPageWithTotal(42));

        PageResponseDTO<ReservationDTO> res = service.getReservationsByCompanyIdWithPageable(3, pageRequest);

        assertThat(res.getTotalCount()).isEqualTo(42);
    }

    @Test
    void 업체ID가_0이면_전체_대상으로_조회한다() {
        when(reservationRepository.findPageByCompanyExcludingStatuses(isNull(), any(), any()))
                .thenReturn(emptyPageWithTotal(5));

        PageResponseDTO<ReservationDTO> res = service.getReservationsByCompanyIdWithPageable(0, pageRequest);

        assertThat(res.getTotalCount()).isEqualTo(5);
    }

    @Test
    void 취소요청_목록은_CANCEL_REQUEST만_조회한다() {
        when(reservationRepository.findPageByCompanyWithStatuses(isNull(),
                eq(List.of(ReservationStatus.CANCEL_REQUEST)), any()))
                .thenReturn(emptyPageWithTotal(2));

        PageResponseDTO<ReservationDTO> res = service.getCancelRequestReservationsByCompanyIdWithPageable(null, pageRequest);

        assertThat(res.getTotalCount()).isEqualTo(2);
    }

    @Test
    void 숙소별_활성목록도_같은_필터를_쓴다() {
        when(reservationRepository.findPageByAccommodationExcludingStatuses(eq(10L), any(), any()))
                .thenReturn(emptyPageWithTotal(7));

        PageResponseDTO<ReservationDTO> res = service.getReservationsByAccommodationIdWithPageable(10L, pageRequest);

        assertThat(res.getTotalCount()).isEqualTo(7);
        verify(reservationRepository).findPageByAccommodationExcludingStatuses(eq(10L),
                eq(OReservationService.INACTIVE_STATUSES), any());
    }
}
