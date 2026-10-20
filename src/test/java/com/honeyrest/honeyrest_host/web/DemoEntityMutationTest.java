package com.honeyrest.honeyrest_host.web;

import com.honeyrest.domain.entity.Company;
import com.honeyrest.domain.entity.Review;
import com.honeyrest.domain.entity.Room;
import com.honeyrest.domain.entity.RoomImage;
import com.honeyrest.domain.type.ReviewStatus;
import com.honeyrest.honeyrest_host.dtoAdmin.ReviewDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomImageDTO;
import com.honeyrest.honeyrest_host.repositoryOwner.OCompanyRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OReviewRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomImageRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.ORoomRepository;
import com.honeyrest.honeyrest_host.serviceAdmin.ReviewService;
import com.honeyrest.honeyrest_host.serviceAdmin.RoomImageService;
import com.honeyrest.honeyrest_host.serviceAdmin.RoomService;
import com.honeyrest.honeyrest_host.serviceOwner.ORoomService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 리뷰·객실 이미지·JSON 컬럼 쓰기 경로 검증. demo 프로필(H2 + 시드)에서 실제 서비스로 확인한다.
 * (DemoAdminScreensTest 와 같은 설정이라 Spring 컨텍스트를 공유한다)
 */
@SpringBootTest(properties = {
        "DEMO_COMPANY_PASSWORD=Demo1234!",
        "DEMO_ADMIN_PASSWORD=Demo1234!",
        "APP_STORAGE_LOCAL_DIR=build/test-uploads-demo"
})
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@DisplayName("demo 프로필: 리뷰·객실·JSON 컬럼 쓰기 경로 회귀")
class DemoEntityMutationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private OReviewRepository reviewRepository;
    @Autowired private ORoomRepository roomRepository;
    @Autowired private ORoomImageRepository roomImageRepository;
    @Autowired private OCompanyRepository companyRepository;
    @Autowired private ReviewService adminReviewService;
    @Autowired private RoomService adminRoomService;
    @Autowired private RoomImageService adminRoomImageService;
    @Autowired private ORoomService ownerRoomService;
    @Autowired private EntityManager em;
    @Autowired private TransactionTemplate tx;

    private static RequestPostProcessor superAdmin() {
        return user("admin@honeyrest.com").roles("SUPER_ADMIN");
    }

    private static RequestPostProcessor companyAdmin() {
        return user("contact@honeyrest.com").roles("COMPANY_ADMIN");
    }

    /** 업체 1(데모 업체 관리자) 소유 리뷰 중 가장 마지막 것 (토글 테스트가 쓰는 첫 리뷰와 겹치지 않게) */
    private Review companyReview() {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getAccommodationId() <= 3L)
                .max(Comparator.comparing(Review::getReviewId)).orElseThrow();
    }

    // ===================== 리뷰 =====================

    @Test
    @DisplayName("리뷰 상세 저장(답변·상태)은 작성일·좋아요·본문·평점을 보존한다 (과거: 빌더 merge 로 null 덮어쓰기)")
    void reviewPatchPreservesOtherFields() throws Exception {
        Review before = companyReview();
        Long id = before.getReviewId();

        mockMvc.perform(post("/admin/reviews/detail/" + id)
                        .param("reviewId", id.toString()).param("status", "HIDDEN").param("reply", "답변 감사합니다")
                        .with(companyAdmin()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));

        Review after = reviewRepository.findByReviewId(id);
        assertThat(after.getStatus()).isEqualTo(ReviewStatus.HIDDEN);
        assertThat(after.getReply()).isEqualTo("답변 감사합니다");
        assertThat(after.getCreatedAt()).isEqualTo(before.getCreatedAt());
        assertThat(after.getLikeCount()).isEqualTo(before.getLikeCount());
        assertThat(after.getContent()).isEqualTo(before.getContent());
        assertThat(after.getRating()).isEqualByComparingTo(before.getRating());
        assertThat(after.getReservation().getReservationId()).isEqualTo(before.getReservation().getReservationId());

        // 답변을 비우고 저장하면 답변 삭제(null), 노출로 되돌리면 PUBLISHED
        adminReviewService.patch(id, ReviewDTO.builder().reply("").status("VISIBLE").build());
        Review cleared = reviewRepository.findByReviewId(id);
        assertThat(cleared.getReply()).isNull();
        assertThat(cleared.getStatus()).isEqualTo(ReviewStatus.PUBLISHED);
        assertThat(cleared.getCreatedAt()).isEqualTo(before.getCreatedAt());
    }

    @Test
    @DisplayName("리뷰 상태는 공유 목록(PUBLISHED/HIDDEN/DELETED)만 허용: PENDING·REPORTED 는 거부, 숨김은 HIDDEN")
    void reviewStatusUsesSharedList() throws Exception {
        Long id = companyReview().getReviewId();
        String original = reviewRepository.findByReviewId(id).getStatus();

        for (String rejected : List.of("PENDING", "REPORTED")) {
            mockMvc.perform(post("/admin/reviews/" + id + "/status").param("value", rejected)
                            .with(companyAdmin()).with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attributeExists("error"));
            assertThat(reviewRepository.findByReviewId(id).getStatus()).isEqualTo(original);
        }
        assertThatThrownBy(() -> adminReviewService.patch(id, ReviewDTO.builder().status("PENDING").build()))
                .isInstanceOf(IllegalArgumentException.class);

        adminReviewService.changeStatus(id, "deleted");
        assertThat(reviewRepository.findByReviewId(id).getStatus()).isEqualTo(ReviewStatus.DELETED);
        adminReviewService.delete(id); // 소프트 삭제 = HIDDEN
        assertThat(reviewRepository.findByReviewId(id).getStatus()).isEqualTo(ReviewStatus.HIDDEN);
        adminReviewService.changeStatus(id, ReviewStatus.PUBLISHED);

        // 리뷰 식별 정보(예약·숙소·객실)는 바꿀 수 없다
        assertThatThrownBy(() -> adminReviewService.patch(id, ReviewDTO.builder().roomId(999_999L).build()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("리뷰 목록 상태 필터: 삭제(DELETED) 선택지가 있고, 옛 값(PENDING)이 와도 오류 없이 빈 결과")
    void reviewListStatusFilter() throws Exception {
        mockMvc.perform(get("/admin/reviews/list").with(companyAdmin()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<option value=\"DELETED\"")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("value=\"PENDING\""))))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("value=\"REPORTED\""))));
        mockMvc.perform(get("/admin/reviews/list?status=PENDING").with(companyAdmin()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/admin/reviews/list?status=DELETED").with(companyAdmin()))
                .andExpect(status().isOk());
    }

    // ===================== 객실 =====================

    private Long newRoomWithImages(String name) {
        RoomDTO created = adminRoomService.registerRoom(RoomDTO.builder()
                .accommodationId(1L).roomName(name).type("스탠다드").price(new BigDecimal("90000"))
                .maxOccupancy(2).standardOccupancy(2).totalRooms(1).bedInfo("더블 침대 1개").amenities("WiFi").build());
        Long roomId = created.getRoomId();
        adminRoomImageService.addImages(roomId, List.of(
                RoomImageDTO.builder().imageUrl("/uploads/rooms/qa-1.png").build(),
                RoomImageDTO.builder().imageUrl("/uploads/rooms/qa-2.png").build()));
        return roomId;
    }

    private int imageCount(Long roomId) {
        return roomImageRepository.findByRoom_RoomId(roomId).size();
    }

    @Test
    @DisplayName("객실 수정(관리자·총관리자·상태 변경)은 이미지를 잃지 않고, 같은 트랜잭션의 영속 Room 이미지 컬렉션도 유지된다")
    void roomUpdatesKeepImages() {
        Long roomId = newRoomWithImages("QA 이미지 유지 객실");
        LocalDateTime createdAt = roomRepository.findByRoomId(roomId).getCreatedAt();
        assertThat(imageCount(roomId)).isEqualTo(2);

        // 업체 관리자 수정: 같은 트랜잭션에서 영속 Room 의 images 가 비지 않는다
        Integer inSession = tx.execute(s -> {
            adminRoomService.modifyRoom(RoomDTO.builder().roomId(roomId).description("설명 수정").build());
            return em.find(Room.class, roomId).getImages().size();
        });
        assertThat(inSession).isEqualTo(2);
        assertThat(imageCount(roomId)).isEqualTo(2);
        Room modified = roomRepository.findByRoomId(roomId);
        assertThat(modified.getDescription()).isEqualTo("설명 수정");
        assertThat(modified.getName()).isEqualTo("QA 이미지 유지 객실"); // 넘기지 않은 값은 유지
        assertThat(modified.getBedInfo()).isEqualTo("[\"더블 침대 1개\"]");
        assertThat(modified.getCreatedAt()).isEqualTo(createdAt);

        adminRoomService.deactivateRoom(roomId);
        assertThat(roomRepository.findByRoomId(roomId).getStatus()).isEqualTo("INACTIVE");
        assertThat(imageCount(roomId)).isEqualTo(2);

        ownerRoomService.activateRoom(roomId);
        assertThat(roomRepository.findByRoomId(roomId).getStatus()).isEqualTo("ACTIVE");
        assertThat(roomRepository.findByRoomId(roomId).getBedInfo()).isEqualTo("[\"더블 침대 1개\"]");
        assertThat(imageCount(roomId)).isEqualTo(2);

        // 총관리자 수정 폼(전체 항목)
        ownerRoomService.modifyRoom(com.honeyrest.honeyrest_host.dtoOwner.RoomDTO.builder()
                .roomId(roomId).accommodationId(1L).name("QA 이미지 유지 객실 2").type("스탠다드")
                .price(new BigDecimal("95000")).maxOccupancy(2).standardOccupancy(2).totalRooms(1)
                .bedInfo("더블 침대 1개").amenities("WiFi, 욕조").status("ACTIVE").build());
        Room ownerModified = roomRepository.findByRoomId(roomId);
        assertThat(ownerModified.getName()).isEqualTo("QA 이미지 유지 객실 2");
        assertThat(ownerModified.getAmenities()).isEqualTo("[\"WiFi\",\"욕조\"]");
        assertThat(ownerModified.getCreatedAt()).isEqualTo(createdAt);
        assertThat(imageCount(roomId)).isEqualTo(2);

        ownerRoomService.removeRoom(roomId); // 총관리자 삭제 = 판매 중지(INACTIVE)
        assertThat(imageCount(roomId)).isEqualTo(2);
    }

    @Test
    @DisplayName("이미지 개별 삭제·객실 삭제는 cascade=ALL 에서도 중복 삭제 없이 동작한다")
    void explicitImageDeletionStillWorks() {
        Long roomId = newRoomWithImages("QA 이미지 삭제 객실");
        RoomImage first = roomImageRepository.findByRoom_RoomId(roomId).get(0);

        adminRoomImageService.deleteImage(first.getImageId());
        assertThat(imageCount(roomId)).isEqualTo(1);

        // 객실 수정 후 같은 트랜잭션에서 이미지를 지워도(영속 Room 의 images 가 로딩된 상태) 다시 저장되지 않는다
        Long remainingId = roomImageRepository.findByRoom_RoomId(roomId).get(0).getImageId();
        tx.executeWithoutResult(s -> {
            adminRoomService.modifyRoom(RoomDTO.builder().roomId(roomId).description("삭제 전 수정").build());
            em.find(Room.class, roomId).getImages().size();
            adminRoomImageService.deleteImage(remainingId);
        });
        assertThat(imageCount(roomId)).isZero();

        // 이미지를 다시 붙이고 객실 삭제: 서비스의 이미지 선삭제 + cascade REMOVE 가 겹쳐도 실패하지 않는다
        adminRoomImageService.addImages(roomId, List.of(RoomImageDTO.builder().imageUrl("/uploads/rooms/qa-3.png").build()));
        assertThat(imageCount(roomId)).isEqualTo(1);
        adminRoomService.removeRoom(roomId);
        assertThat(roomRepository.findById(roomId)).isEmpty();
        assertThat(imageCount(roomId)).isZero();
    }

    // ===================== JSON 텍스트 컬럼 =====================

    @Test
    @DisplayName("시드의 JSON 텍스트 컬럼은 따옴표로 감싸이지 않은 원문 그대로다 (VARCHAR ALTER 제거 후)")
    void seedJsonColumnsArePlainText() {
        assertThat(roomRepository.findByRoomId(1L).getBedInfo()).isEqualTo("[\"더블 침대 1개\"]");
        assertThat(roomRepository.findByRoomId(1L).getAmenities()).isEqualTo("[\"WiFi\",\"욕조\"]");
        assertThat(companyRepository.findById(1).orElseThrow().getBankInfo())
                .isEqualTo("{\"bank\":\"국민은행\",\"account\":\"123-456-789012\",\"holder\":\"허니레스트 스테이\"}");
    }

    @Test
    @DisplayName("업체 계좌 정보를 두 번 저장해도 따옴표가 쌓이지 않는다")
    void bankInfoRoundTrip() throws Exception {
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/owner/company/modify")
                            .param("companyId", "2").param("name", "씨사이드 호텔").param("businessNumber", "234-56-78901")
                            .param("ownerName", "김바다").param("phone", "051-987-6543").param("email", "info@seasidehotel.com")
                            .param("address", "부산광역시 해운대구 해운대해변로 30").param("commissionRate", "12.00")
                            .param("status", "ACTIVE").param("bankInfo", "부산은행, 101-2020-3030-04, 씨사이드 호텔")
                            .with(superAdmin()).with(csrf()))
                    .andExpect(status().is3xxRedirection());
        }
        Company company = companyRepository.findById(2).orElseThrow();
        assertThat(company.getBankInfo())
                .isEqualTo("{\"bank\":\"부산은행\",\"account\":\"101-2020-3030-04\",\"holder\":\"씨사이드 호텔\"}");
        mockMvc.perform(get("/owner/company/2/modify").with(superAdmin()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("부산은행, 101-2020-3030-04, 씨사이드 호텔")));
    }
}
