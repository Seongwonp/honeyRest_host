package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.domain.entity.*;
import com.honeyrest.domain.type.ReservationStatus;
import com.honeyrest.honeyrest_host.dtoAdmin.CalendarCellDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.PriceCalendarDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomCalendarDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.PriceCalendarService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 화면 폼 ↔ 컨트롤러 파라미터 이름 회귀 테스트 (test 프로필 = H2).
 * <ul>
 *   <li>문의 답변: 과거 @RequestParam("relpyText") 오타로 화면(replyText/reply)이 보내는 값과 달라 항상 400</li>
 *   <li>가격 캘린더 수정: 과거 yearMonth 를 요구했지만 모달은 ym 을 보내 항상 400.
 *       또 모달 기본값이 설정 가격이 아니라 그날 체크인 매출 합계였다</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminFormBindingTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EntityManager em;
    @Autowired private PriceCalendarService priceCalendarService;

    private String adminEmail;
    private Company company;
    private Accommodation accommodation;
    private Room room;
    private User guest;

    @BeforeEach
    void setUp() {
        JpaTestFixtures fx = new JpaTestFixtures(em);
        adminEmail = "form-admin-" + UUID.randomUUID().toString().substring(0, 8) + "@test.honeyrest.com";
        company = Company.builder()
                .name("폼 업체").businessNumber("000-00-" + UUID.randomUUID().toString().substring(0, 5))
                .email(adminEmail).status("APPROVED")
                .build();
        em.persist(company);
        accommodation = fx.accommodation(company, "폼 숙소");
        room = fx.room(accommodation, "폼 객실", 3); // room.price = 100,000
        guest = fx.user("문의고객");
        fx.flushAndClear();
    }

    private RequestPostProcessor admin() {
        return SecurityMockMvcRequestPostProcessors.user(adminEmail).roles("COMPANY_ADMIN");
    }

    @Test
    void 문의_답변은_replyText_파라미터로_저장된다() throws Exception {
        Inquiry inquiry = Inquiry.builder()
                .user(em.find(User.class, guest.getUserId()))
                .accommodation(em.find(Accommodation.class, accommodation.getAccommodationId()))
                .title("주차 문의").content("주차 가능한가요?").category("기타").isReplied(false)
                .build();
        em.persist(inquiry);
        em.flush();

        mockMvc.perform(post("/admin/inquiries/{id}/reply", inquiry.getInquiryId())
                        .param("replyText", "네, 무료 주차 가능합니다.")
                        .with(admin()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/inquiries/list?**"));

        em.flush();
        em.clear();
        Inquiry saved = em.find(Inquiry.class, inquiry.getInquiryId());
        assertThat(saved.getReply()).isEqualTo("네, 무료 주차 가능합니다.");
        assertThat(saved.getIsReplied()).isTrue();
    }

    @Test
    void 가격_수정은_모달의_ym_파라미터로_저장되고_같은_달로_돌아간다() throws Exception {
        mockMvc.perform(post("/admin/price/upsert")
                        .param("companyId", "0") // 서버가 로그인 회사로 다시 계산한다
                        .param("roomId", String.valueOf(room.getRoomId()))
                        .param("ym", "2031-05")
                        .param("date", "2031-05-10")
                        .param("price", "123000")
                        .with(admin()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/price/page?*ym=2031-05*"));

        em.clear();
        PriceCalendar saved = em.createQuery(
                        "select pc from PriceCalendar pc where pc.room.roomId = :roomId and pc.date = :date", PriceCalendar.class)
                .setParameter("roomId", room.getRoomId()).setParameter("date", LocalDate.of(2031, 5, 10))
                .getSingleResult();
        assertThat(saved.getPrice()).isEqualByComparingTo("123000");
    }

    @Test
    void 월_캘린더_셀은_설정된_1박가격과_체크인매출을_따로_준다() {
        LocalDate priced = LocalDate.of(2031, 6, 10);
        LocalDate checkin = LocalDate.of(2031, 6, 12);
        em.persist(PriceCalendar.builder().room(em.find(Room.class, room.getRoomId()))
                .date(priced).price(BigDecimal.valueOf(150_000)).availableRoom(3).build());
        JpaTestFixtures fx = new JpaTestFixtures(em);
        fx.reservation(em.find(User.class, guest.getUserId()), em.find(Room.class, room.getRoomId()),
                ReservationStatus.CONFIRMED, checkin, checkin.plusDays(2)); // price = 100,000
        fx.flushAndClear();

        PriceCalendarDTO month = priceCalendarService.getMonth(company.getCompanyId(),
                accommodation.getAccommodationId(), YearMonth.of(2031, 6), null);
        RoomCalendarDTO rc = month.getRooms().get(0);

        CalendarCellDTO pricedCell = rc.getDays().get(priced.getDayOfMonth() - 1);
        assertThat(pricedCell.getPrice()).isEqualByComparingTo("150000"); // price_calendar 설정가
        assertThat(pricedCell.getRevenue()).isNull();

        CalendarCellDTO checkinCell = rc.getDays().get(checkin.getDayOfMonth() - 1);
        assertThat(checkinCell.getPrice()).isEqualByComparingTo("100000"); // 설정 없으면 객실 기본가
        assertThat(checkinCell.getRevenue()).isEqualByComparingTo("100000"); // 체크인 매출은 별도
        assertThat(checkinCell.getAvailable()).isEqualTo(2);
    }
}
