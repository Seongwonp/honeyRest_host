package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoOwner.ReviewDTO;
import com.honeyrest.honeyrest_host.serviceOwner.OAccommodationService;
import com.honeyrest.honeyrest_host.serviceOwner.OCompanyService;
import com.honeyrest.honeyrest_host.serviceOwner.OReviewService;
import com.honeyrest.honeyrest_host.serviceOwner.ORoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller("ownerReviewController")
@RequestMapping("/owner")
@RequiredArgsConstructor
public class ReviewController {
    private final OReviewService reviewService;
    private final OAccommodationService accommodationService;
    private final ORoomService roomService;


    /** 리뷰 목록. 쿼리 파라미터(?accommodationId=)와 경로 /review/accommodation/{id} 를 모두 받는다. */
    @GetMapping({"/review/list", "/review/accommodation/{accommodationId}"})
    public String reviews(@PathVariable(name = "accommodationId", required = false) Long pathAccommodationId,
                          @RequestParam(name = "accommodationId", required = false) Long paramAccommodationId,
                          @RequestParam(required = false) Long roomId,
                          @ModelAttribute PageRequestDTO pageRequestDTO,
                          Model model) {
        Long accommodationId = pathAccommodationId != null ? pathAccommodationId : paramAccommodationId;
        fillList(accommodationId, roomId, pageRequestDTO, model);
        return "owner/review/list";
    }

    /** 업체의 숙소 리뷰: 숙소 선택 목록을 그 업체 것으로 좁힌다 */
    @GetMapping("/review/company/{companyId}")
    public String reviewsOfCompany(@PathVariable Integer companyId,
                                   @RequestParam(required = false) Long accommodationId,
                                   @ModelAttribute PageRequestDTO pageRequestDTO,
                                   Model model) {
        fillList(accommodationId, null, pageRequestDTO, model);
        model.addAttribute("accommodations", accommodationService.getAccommodationsByCompanyId(companyId));
        model.addAttribute("selectedCompanyId", companyId);
        return "owner/review/list";
    }

    @GetMapping("/review/room/{roomId}")
    public String reviewsOfRoom(@PathVariable Long roomId,
                                @ModelAttribute PageRequestDTO pageRequestDTO,
                                Model model) {
        fillList(null, roomId, pageRequestDTO, model);
        return "owner/review/list";
    }

    private void fillList(Long accommodationId, Long roomId, PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<ReviewDTO> reviewPage = reviewService.getReviews(accommodationId, roomId, pageRequestDTO);
        if (accommodationId != null && accommodationId > 0) {
            model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationId));
        }
        if (roomId != null && roomId > 0) {
            model.addAttribute("room", roomService.getByRoomId(roomId));
        }
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("selectedAccommodationId", accommodationId);
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("reviews", reviewPage.getDtoList());
        model.addAttribute("pageResponse", reviewPage);
    }

    /** 답글·노출 상태 관리 화면 (리뷰 본문·평점은 작성자 소유라 읽기 전용) */
    @GetMapping("/review/{reviewId}/modify")
    public String modifyReview(@PathVariable Long reviewId, Model model) {
        model.addAttribute("reviewId", reviewId);
        model.addAttribute("review", reviewService.getReviewById(reviewId));
        return "owner/review/modify";
    }

    /** 답글·노출 상태 저장 */
    @PostMapping("/review/{reviewId}/reply")
    public String saveReply(@PathVariable Long reviewId,
                            @RequestParam(required = false) String reply,
                            @RequestParam(required = false) String status,
                            RedirectAttributes ra) {
        reviewService.saveReply(reviewId, reply, status);
        ra.addFlashAttribute("success", "답글과 노출 상태를 저장했습니다.");
        return "redirect:/owner/review/" + reviewId + "/modify";
    }

    /** 숨김 처리(소프트 삭제) */
    @PostMapping("/review/{reviewId}/delete")
    public String hideReview(@PathVariable Long reviewId, RedirectAttributes ra) {
        reviewService.hide(reviewId);
        ra.addFlashAttribute("success", "리뷰를 숨김 처리했습니다.");
        return "redirect:/owner/review/list";
    }
}
