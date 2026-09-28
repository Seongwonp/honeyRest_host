package com.honeyrest.honeyrest_host.security;

import com.honeyrest.honeyrest_host.entity.*;
import com.honeyrest.honeyrest_host.support.JpaTestFixtures;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 메서드 보안(@PreAuthorize("@companyAccess.ownsXxx(#id, authentication)")) 회귀 테스트.
 * <p>
 * 업체 A 의 COMPANY_ADMIN 이 업체 B 의 숙소·객실·예약에 접근하면 컨트롤러 본문에 들어가기 전에 거부되고,
 * GlobalExceptionHandler 를 거쳐 403 + error/403 화면으로 응답해야 한다.
 * (test 프로필 = H2, MockMvc 요청이 테스트 트랜잭션에 참여하므로 픽스처는 롤백으로 정리된다.)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CompanyAccessMethodSecurityTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EntityManager em;

    private String adminAEmail;
    private Accommodation accA;
    private Accommodation accB;
    private Room roomB;
    private Reservation reservationB;

    @BeforeEach
    void setUp() {
        JpaTestFixtures fx = new JpaTestFixtures(em);
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        adminAEmail = "company-a-" + suffix + "@test.honeyrest.com";

        // 업체 ↔ 관리자 계정은 FK 없이 이메일로 매칭된다 (CompanyRepository.findCompanyByEmail)
        Company companyA = company("업체A", adminAEmail);
        Company companyB = company("업체B", "company-b-" + suffix + "@test.honeyrest.com");

        accA = fx.accommodation(companyA, "A 숙소");
        accB = fx.accommodation(companyB, "B 숙소");
        roomB = fx.room(accB, "B 객실", 2);
        User guest = fx.user("손님");
        reservationB = fx.reservation(guest, roomB, ReservationStatus.CONFIRMED,
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(4));
        fx.flushAndClear();
    }

    private Company company(String name, String email) {
        Company c = Company.builder()
                .name(name)
                .businessNumber("000-00-" + UUID.randomUUID().toString().substring(0, 5))
                .email(email)
                .status("APPROVED")
                .build();
        em.persist(c);
        return c;
    }

    private RequestPostProcessor adminA() {
        return SecurityMockMvcRequestPostProcessors.user(adminAEmail).roles("COMPANY_ADMIN");
    }

    @Test
    void 다른_업체_숙소_상세는_403() throws Exception {
        mockMvc.perform(get("/admin/accommodations/detail/{id}", accB.getAccommodationId()).with(adminA()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("error/403"));
    }

    @Test
    void 다른_업체_숙소_삭제는_403이고_숙소는_남아있다() throws Exception {
        mockMvc.perform(post("/admin/accommodations/{id}/delete", accB.getAccommodationId())
                        .with(adminA()).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("error/403"));

        assertThat(em.find(Accommodation.class, accB.getAccommodationId())).isNotNull();
    }

    @Test
    void 다른_업체_객실_상세는_403() throws Exception {
        mockMvc.perform(get("/admin/rooms/detail/{roomId}", roomB.getRoomId()).with(adminA()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("error/403"));
    }

    @Test
    void 다른_업체_예약_취소는_403이고_상태는_그대로다() throws Exception {
        mockMvc.perform(post("/admin/reservations/{reservationId}/cancel", reservationB.getReservationId())
                        .with(adminA()).with(csrf()))
                .andExpect(status().isForbidden());

        em.clear();
        assertThat(em.find(Reservation.class, reservationB.getReservationId()).getStatus())
                .isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void 다른_업체_객실_가격_수정은_403() throws Exception {
        mockMvc.perform(post("/admin/price/upsert")
                        .param("companyId", "1")
                        .param("roomId", String.valueOf(roomB.getRoomId()))
                        .param("yearMonth", "2026-10")
                        .param("date", "2026-10-01")
                        .param("price", "1")
                        .with(adminA()).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void 자기_업체_숙소는_통과한다() throws Exception {
        // 승인 요청(PENDING 제출)은 본문 실행 후 목록으로 리다이렉트한다 (403 이 아님을 확인)
        mockMvc.perform(post("/admin/accommodations/{id}/request", accA.getAccommodationId())
                        .with(adminA()).with(csrf()))
                .andExpect(status().is3xxRedirection());

        em.clear();
        assertThat(em.find(Accommodation.class, accA.getAccommodationId()).getStatus()).isEqualTo("PENDING");
    }

    @Test
    void 존재하지_않는_ID도_403으로_막는다() throws Exception {
        mockMvc.perform(get("/admin/accommodations/detail/{id}", 987654321L).with(adminA()))
                .andExpect(status().isForbidden());
    }
}
