package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.honeyrest_host.entity.*;
import com.honeyrest.honeyrest_host.support.JpaTestFixtures;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OReservationRepository 의 상태 포함/제외 + 페이징 JPQL(@Query) 4종을 실제 JPA 컨텍스트(H2)에서 검증한다.
 * 목록(content)과 total(countQuery)이 같은 조건을 쓰는지, 상태 비교가 대소문자를 무시하는지,
 * companyId/accommodationId 가 null 이면 전체 대상인지 확인한다.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OReservationRepositoryStatusQueryTest {

    private static final Set<String> INACTIVE = Set.of(ReservationStatus.CANCELLED, ReservationStatus.CANCEL_REQUEST);
    private static final Set<String> CANCEL_REQ = Set.of(ReservationStatus.CANCEL_REQUEST);

    @Autowired
    private OReservationRepository repository;

    @Autowired
    private EntityManager em;

    private Company companyA;
    private Accommodation accA1;
    private Accommodation accA2;

    @BeforeEach
    void setUp() {
        JpaTestFixtures fx = new JpaTestFixtures(em);
        User user = fx.user("테스트 고객");
        LocalDate in = LocalDate.now().plusDays(3);
        LocalDate out = in.plusDays(1);

        companyA = fx.company("업체 A");
        accA1 = fx.accommodation(companyA, "A-1 숙소");
        accA2 = fx.accommodation(companyA, "A-2 숙소");
        Room roomA1 = fx.room(accA1, "A-1 객실", 10);
        Room roomA2 = fx.room(accA2, "A-2 객실", 10);

        Company companyB = fx.company("업체 B");
        Room roomB = fx.room(fx.accommodation(companyB, "B 숙소"), "B 객실", 10);

        // 업체 A / 숙소 A-1: 활성 3건(소문자 상태 포함), 비활성 3건
        fx.reservation(user, roomA1, ReservationStatus.CONFIRMED, in, out);
        fx.reservation(user, roomA1, "pending", in, out);           // 대소문자 무시 확인용
        fx.reservation(user, roomA1, ReservationStatus.COMPLETED, in, out);
        fx.reservation(user, roomA1, ReservationStatus.CANCELLED, in, out);
        fx.reservation(user, roomA1, "cancel_request", in, out);    // 대소문자 무시 확인용
        fx.reservation(user, roomA1, ReservationStatus.CANCEL_REQUEST, in, out);
        // 업체 A / 숙소 A-2: 활성 1건, 취소요청 1건
        fx.reservation(user, roomA2, ReservationStatus.CONFIRMED, in, out);
        fx.reservation(user, roomA2, ReservationStatus.CANCEL_REQUEST, in, out);
        // 업체 B: 활성 2건, 취소 1건
        fx.reservation(user, roomB, ReservationStatus.CONFIRMED, in, out);
        fx.reservation(user, roomB, ReservationStatus.NO_SHOW, in, out);
        fx.reservation(user, roomB, ReservationStatus.CANCELLED, in, out);

        fx.flushAndClear();
    }

    /** 크기 2 페이지로 끝까지 넘기며 모은 content 가 total 과 일치하는지 확인하고 전체 목록을 돌려준다. */
    private List<Reservation> collectAllPages(java.util.function.Function<Pageable, Page<Reservation>> query) {
        List<Reservation> all = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 2, Sort.by("reservationId").descending());
        Page<Reservation> page;
        do {
            page = query.apply(pageable);
            all.addAll(page.getContent());
            pageable = pageable.next();
        } while (page.hasNext());
        assertThat(all).hasSize((int) page.getTotalElements());
        return all;
    }

    private static List<String> upperStatuses(List<Reservation> rows) {
        return rows.stream().map(r -> r.getStatus().toUpperCase()).toList();
    }

    @Test
    void 업체기준_상태제외_목록과_count_가_일치한다() {
        List<Reservation> rows = collectAllPages(p ->
                repository.findPageByCompanyExcludingStatuses(companyA.getCompanyId(), INACTIVE, p));

        assertThat(rows).hasSize(4); // A-1 활성 3 + A-2 활성 1
        assertThat(upperStatuses(rows)).doesNotContainAnyElementsOf(INACTIVE);
        assertThat(rows).allMatch(r -> r.getAccommodation().getCompany().getCompanyId().equals(companyA.getCompanyId()));
    }

    @Test
    void 업체기준_상태포함_목록과_count_가_일치한다() {
        List<Reservation> rows = collectAllPages(p ->
                repository.findPageByCompanyWithStatuses(companyA.getCompanyId(), CANCEL_REQ, p));

        assertThat(rows).hasSize(3); // A-1 취소요청 2(대소문자 혼용) + A-2 1
        assertThat(upperStatuses(rows)).containsOnly(ReservationStatus.CANCEL_REQUEST);
    }

    @Test
    void 숙소기준_상태제외_와_상태포함() {
        List<Reservation> active = collectAllPages(p ->
                repository.findPageByAccommodationExcludingStatuses(accA1.getAccommodationId(), INACTIVE, p));
        List<Reservation> cancelReq = collectAllPages(p ->
                repository.findPageByAccommodationWithStatuses(accA2.getAccommodationId(), CANCEL_REQ, p));

        assertThat(active).hasSize(3);
        assertThat(active).allMatch(r -> r.getAccommodation().getAccommodationId().equals(accA1.getAccommodationId()));
        assertThat(cancelReq).hasSize(1);
        assertThat(cancelReq.get(0).getAccommodation().getAccommodationId()).isEqualTo(accA2.getAccommodationId());
    }

    @Test
    void 범위_ID가_null_이면_전체_대상() {
        List<Reservation> allActive = collectAllPages(p ->
                repository.findPageByCompanyExcludingStatuses(null, INACTIVE, p));
        List<Reservation> allCancelReq = collectAllPages(p ->
                repository.findPageByAccommodationWithStatuses(null, CANCEL_REQ, p));

        assertThat(allActive).hasSize(6);    // 업체 A 4 + 업체 B 2
        assertThat(allCancelReq).hasSize(3); // 업체 B 에는 취소요청 없음
    }
}
