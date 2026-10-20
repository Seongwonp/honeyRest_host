package com.honeyrest.honeyrest_host.web;

import com.honeyrest.domain.entity.Accommodation;
import com.honeyrest.domain.entity.Company;
import com.honeyrest.domain.entity.Coupon;
import com.honeyrest.domain.entity.Review;
import com.honeyrest.domain.entity.Room;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomDTO;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OCompanyRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OCouponRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OReviewRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomRepository;
import com.honeyrest.honeyrest_host.serviceAdmin.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * demo 프로필(H2 + 시드)에서 관리자 화면이 렌더링되고 주요 액션이 동작하는지 확인한다.
 */
@SpringBootTest(properties = {
        "DEMO_COMPANY_PASSWORD=Demo1234!",
        "DEMO_ADMIN_PASSWORD=Demo1234!",
        "APP_STORAGE_LOCAL_DIR=build/test-uploads-demo"
})
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@DisplayName("demo 프로필: 관리자 화면 렌더링·액션 회귀")
class DemoAdminScreensTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private OReviewRepository reviewRepository;
    @Autowired private ORoomRepository roomRepository;
    @Autowired private OAccommodationRepository accommodationRepository;
    @Autowired private OCompanyRepository companyRepository;
    @Autowired private OCouponRepository couponRepository;
    @Autowired private RoomService adminRoomService;
    @Autowired private com.honeyrest.honeyrest_host.repositoryOwner.ORoomImageRepository roomImageRepository;

    private static RequestPostProcessor superAdmin() {
        return user("admin@honeyrest.com").roles("SUPER_ADMIN");
    }

    private static RequestPostProcessor companyAdmin() {
        return user("contact@honeyrest.com").roles("COMPANY_ADMIN");
    }

    /** 200 + 화면 렌더링 성공 + 뷰 이름이 "/" 로 시작하지 않음(실행 jar 에서 템플릿 해석 실패) */
    private void assertPageRenders(String url, RequestPostProcessor who) throws Exception {
        MvcResult result = mockMvc.perform(get(url).with(who)).andReturn();
        assertThat(result.getResponse().getStatus()).as(url).isEqualTo(200);
        assertThat(result.getModelAndView()).as(url).isNotNull();
        assertThat(result.getModelAndView().getViewName()).as(url + " 뷰 이름").doesNotStartWith("/");
        assertThat(result.getResponse().getContentAsString()).as(url).contains("</html>");
    }

    @ParameterizedTest(name = "총관리자 {0}")
    @ValueSource(strings = {
            "/owner/dashboard",
            "/owner/company/list", "/owner/company/list?keyword=허니", "/owner/company/inActive/list?page=0&size=10",
            "/owner/company/create", "/owner/company/1/modify",
            "/owner/accommodation/list?companyId=1", "/owner/accommodation/inActive/list?page=1&size=10&companyId=0",
            "/owner/accommodation/create", "/owner/accommodation/1/modify",
            "/owner/room/list", "/owner/room/list?accommodationId=1", "/owner/room/inActive/list", "/owner/accommodation/1/rooms",
            "/owner/room/create", "/owner/room/1/modify",
            "/owner/reservation/list", "/owner/reservation/list?page=0&size=10&companyId=",
            "/owner/reservation/list?accommodationId=1", "/owner/reservation/list?roomId=1",
            "/owner/reservation/inActive/list", "/owner/reservation/create", "/owner/reservation/2/modify",
            "/owner/user/list", "/owner/user/list?keyword=김",
            "/owner/review/list", "/owner/review/list?accommodationId=1", "/owner/review/room/1",
            "/owner/coupon/list", "/owner/coupon/create",
            "/owner/sales/day", "/owner/sales/month", "/owner/sales/day?companyId=1", "/owner/sales/month/accommodation/1",
            "/owner/system/errors", "/owner/system/errors?filter=unresolved",
            "/owner/calendar/accommodation/1/calendar", "/owner/calendar/company/1/calendar"
    })
    void ownerScreensRender(String url) throws Exception {
        assertPageRenders(url, superAdmin());
    }

    @ParameterizedTest(name = "업체 관리자 {0}")
    @ValueSource(strings = {
            "/admin/dashboard", "/admin/accommodations/list?page=-1&size=12", "/admin/accommodations/list?q=제주",
            "/admin/accommodations/add", "/admin/rooms/list_all", "/admin/rooms/detail/1",
            "/admin/price/page?companyId=1&ym=2026-02&accommodationId=1&roomId=1",
            "/admin/reservations/my", "/admin/reservations/new", "/admin/reservations/day",
            "/admin/reservations/cancel-requests?page=1&size=10",
            "/admin/point/list?page=-1&size=10", "/admin/point/refunds",
            "/admin/reviews/list?view=grid&page=0&size=10", "/admin/reviews/list?status=PUBLISHED",
            "/admin/inquiries/list", "/admin/reports/sales"
    })
    void companyScreensRender(String url) throws Exception {
        assertPageRenders(url, companyAdmin());
    }

    @Test
    @DisplayName("총관리자 리뷰 답글: 저장 경로가 /owner/review/{id}/reply 이고 작성일·좋아요가 보존된다")
    void ownerReviewReply() throws Exception {
        Review review = reviewRepository.findAll().stream().min(Comparator.comparing(Review::getReviewId)).orElseThrow();
        LocalDateTime createdAt = review.getCreatedAt();

        mockMvc.perform(post("/owner/review/" + review.getReviewId() + "/reply")
                        .param("reply", "QA 답글").param("status", "PUBLISHED")
                        .with(superAdmin()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));

        Review saved = reviewRepository.findByReviewId(review.getReviewId());
        assertThat(saved.getReply()).isEqualTo("QA 답글");
        assertThat(saved.getStatus()).isEqualTo("PUBLISHED");
        assertThat(saved.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("총관리자 객실 '판매 중지'는 행을 지우지 않고 INACTIVE 로 바꾼다 (과거: 없는 경로 404)")
    void ownerRoomDeactivate() throws Exception {
        mockMvc.perform(post("/owner/room/5/delete").with(superAdmin()).with(csrf()))
                .andExpect(status().is3xxRedirection());
        assertThat(roomRepository.findByRoomId(5L).getStatus()).isEqualTo("INACTIVE");

        mockMvc.perform(post("/owner/room/5/activate").with(superAdmin()).with(csrf()))
                .andExpect(status().is3xxRedirection());
        assertThat(roomRepository.findByRoomId(5L).getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("승인 대기 숙소를 수정 폼으로 ACTIVE 로 올릴 수 없다 (승인 버튼만 ACTIVE 를 만든다)")
    void ownerAccommodationModifyCannotBypassApproval() throws Exception {
        Accommodation pending = accommodationRepository.findByAccommodationId(3L);
        assertThat(pending.getStatus()).isEqualTo("PENDING");

        mockMvc.perform(multipart("/owner/accommodation/modify")
                        .param("accommodationId", "3")
                        .param("companyId", String.valueOf(pending.getCompany().getCompanyId()))
                        .param("categoryId", String.valueOf(pending.getCategory().getCategoryId()))
                        .param("mainRegionId", String.valueOf(pending.getMainRegion().getRegionId()))
                        .param("subRegionId", String.valueOf(pending.getSubRegion().getRegionId()))
                        .param("name", pending.getName()).param("address", pending.getAddress())
                        .param("checkInTime", "2025-01-01T15:00").param("checkOutTime", "2025-01-01T11:00")
                        .param("minPrice", "150000").param("status", "ACTIVE")
                        .with(superAdmin()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));

        assertThat(accommodationRepository.findByAccommodationId(3L).getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("업체 은행 정보는 JSON 객체로 저장하고 '은행, 계좌, 예금주' 로 보여 준다")
    void ownerCompanyBankInfo() throws Exception {
        Company c = companyRepository.findById(2).orElseThrow();
        mockMvc.perform(post("/owner/company/modify")
                        .param("companyId", "2").param("name", c.getName()).param("businessNumber", c.getBusinessNumber())
                        .param("ownerName", c.getOwnerName()).param("phone", c.getPhone()).param("email", c.getEmail())
                        .param("address", c.getAddress()).param("bankInfo", "부산은행, 101-2020, 씨사이드 호텔")
                        .param("commissionRate", "12").param("status", "ACTIVE")
                        .with(superAdmin()).with(csrf()))
                .andExpect(status().is3xxRedirection());

        String json = companyRepository.findById(2).orElseThrow().getBankInfo();
        assertThat(json).isEqualTo("{\"bank\":\"부산은행\",\"account\":\"101-2020\",\"holder\":\"씨사이드 호텔\"}");

        String html = mockMvc.perform(get("/owner/company/2/modify").with(superAdmin()))
                .andReturn().getResponse().getContentAsString();
        assertThat(html).contains("value=\"부산은행, 101-2020, 씨사이드 호텔\"");
    }

    @Test
    @DisplayName("쿠폰 등록: 활성 체크박스가 바인딩되고, 사용자 API 가 적용하지 않는 대상 유형은 거부한다")
    void ownerCouponCreate() throws Exception {
        mockMvc.perform(post("/owner/coupon/create")
                        .param("name", "QA 쿠폰").param("code", "QA-ACTIVE-1").param("discountType", "PERCENTAGE")
                        .param("discountValue", "10").param("startDate", "2026-01-01T00:00").param("endDate", "2027-01-01T00:00")
                        .param("targetType", "ALL").param("isActive", "on")
                        .with(superAdmin()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));
        Coupon saved = couponRepository.findAll().stream().filter(c -> "QA-ACTIVE-1".equals(c.getCode())).findFirst().orElseThrow();
        assertThat(saved.isActive()).isTrue();

        mockMvc.perform(post("/owner/coupon/create")
                        .param("name", "QA 쿠폰2").param("code", "QA-ROOM-1").param("discountType", "AMOUNT")
                        .param("discountValue", "1000").param("startDate", "2026-01-01T00:00").param("endDate", "2027-01-01T00:00")
                        .param("targetType", "ROOM").param("targetId", "1")
                        .with(superAdmin()).with(csrf()))
                .andExpect(flash().attributeExists("error"));
        assertThat(couponRepository.findAll()).noneMatch(c -> "QA-ROOM-1".equals(c.getCode()));
    }

    @Test
    @DisplayName("객실 침대 정보를 두 번 저장해도 그대로다 (H2 JSON 컬럼 이중 인코딩 회귀)")
    void roomJsonColumnsRoundTrip() throws Exception {
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(multipart("/owner/room/modify")
                            .param("roomId", "6").param("accommodationId", "3").param("name", "솔숲 독채")
                            .param("type", "독채").param("price", "150000").param("maxOccupancy", "6")
                            .param("standardOccupancy", "4").param("totalRooms", "4").param("status", "ACTIVE")
                            .param("bedInfo", "퀸 침대 2개").param("amenities", "WiFi, 바비큐")
                            .with(superAdmin()).with(csrf()))
                    .andExpect(status().is3xxRedirection());
        }
        Room room = roomRepository.findByRoomId(6L);
        assertThat(room.getBedInfo()).isEqualTo("[\"퀸 침대 2개\"]");
        assertThat(room.getAmenities()).isEqualTo("[\"WiFi\",\"바비큐\"]");
    }

    @Test
    @DisplayName("업체 관리자 숙소 등록: 회사 ID 를 입력하지 않아도 로그인 업체로 등록된다 (과거: must not be null)")
    void companyAccommodationAddWithoutCompanyId() throws Exception {
        long before = accommodationRepository.count();
        mockMvc.perform(multipart("/admin/accommodations/add")
                        .file(new MockMultipartFile("file", "a.png", "image/png", new byte[]{1, 2, 3}))
                        .param("categoryId", "1").param("mainRegionId", "1").param("subRegionId", "5")
                        .param("name", "QA 등록 숙소").param("address", "제주시 1")
                        .param("checkInTime", "2025-01-01T15:00").param("checkOutTime", "2025-01-01T11:00")
                        .param("minPrice", "90000")
                        .with(companyAdmin()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));
        assertThat(accommodationRepository.count()).isEqualTo(before + 1);
    }

    @Test
    @DisplayName("업체 관리자 객실 삭제: 예약 없는 객실은 이미지가 있어도 지워진다 (과거: room_image FK 로 실패)")
    void companyRoomDeleteWithImage() throws Exception {
        RoomDTO created = adminRoomService.registerRoom(RoomDTO.builder()
                .accommodationId(1L).roomName("QA 삭제 객실").type("스탠다드").price(new BigDecimal("90000"))
                .maxOccupancy(2).standardOccupancy(2).totalRooms(1).build());
        Long roomId = created.getRoomId();
        roomImageRepository.save(com.honeyrest.domain.entity.RoomImage.builder()
                .room(roomRepository.getReferenceById(roomId)).imageUrl("/uploads/rooms/qa.png").sortOrder(0).build());

        mockMvc.perform(post("/admin/rooms/" + roomId + "/delete").param("accommodationId", "1")
                        .with(companyAdmin()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));
        assertThat(roomRepository.findById(roomId)).isEmpty();
    }

    @Test
    @DisplayName("업체 관리자 리뷰 숨김→노출 토글은 사용자 API 노출 값(PUBLISHED)으로 되돌린다")
    void reviewToggleUsesPublished() throws Exception {
        Review review = reviewRepository.findAll().stream()
                .filter(r -> r.getAccommodationId() <= 3L).min(Comparator.comparing(Review::getReviewId)).orElseThrow();
        Long id = review.getReviewId();
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/admin/reviews/" + id + "/toggle").with(companyAdmin()).with(csrf()))
                    .andExpect(status().is3xxRedirection());
        }
        assertThat(reviewRepository.findByReviewId(id).getStatus()).isEqualTo("PUBLISHED");
    }
}
