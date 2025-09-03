package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoOwner.ReviewDTO;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.service.ReviewService;
import com.honeyrest.honeyrest_host.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final AccommodationService accommodationService;
    private final CompanyService companyService;
    private final RoomService roomService;


    @GetMapping({"/review/list", "/review/accommodation/{accommodationId}"})
    public String reviews(@PathVariable(required = false) Long accommodationId,
                          @ModelAttribute PageRequestDTO pageRequestDTO,
                          Model model) {

        // 모든 숙소 조회 (숙소 선택용)
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        // 리뷰 페이지 조회
        PageResponseDTO<ReviewDTO> reviewPage;

        if (accommodationId != null) {
            // 특정 숙소 리뷰 조회
            Long companyId = companyService.getCompanyIdByAccommodationId(accommodationId);
            model.addAttribute("selectedCompanyId",  companyId);
            reviewPage = reviewService.getReviewsByAccommodationIdWithPageable(accommodationId, pageRequestDTO);
            model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationId));
        } else {
            // 전체 리뷰 조회
            reviewPage = reviewService.getReviewsByAccommodationIdWithPageable(accommodationId ,pageRequestDTO);
        }

        // 모델에 데이터 추가
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodations);

        model.addAttribute("selectedAccommodationId", accommodationId);
        model.addAttribute("reviews", reviewPage.getDtoList());
        model.addAttribute("pageResponse", reviewPage);

        return "owner/review/list";
    }

    @GetMapping( "/review/company/{companyId}")
    public String reviews(@PathVariable(required = false) Long companyId,
                          @RequestParam(required = false) Long accommodationId,
                          @ModelAttribute PageRequestDTO pageRequestDTO,
                          Model model) {

        // 모든 숙소 조회 (숙소 선택용)
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        // 리뷰 페이지 조회
        PageResponseDTO<ReviewDTO> reviewPage;

        if (companyId != null) {
            // 특정 숙소 리뷰 조회
            reviewPage = reviewService.getReviewsByAccommodationIdWithPageable(companyId, pageRequestDTO);
            model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationId));
        } else {
            // 전체 리뷰 조회
            reviewPage = reviewService.getReviewsByAccommodationIdWithPageable(companyId ,pageRequestDTO);
        }

        // 모델에 데이터 추가
        model.addAttribute("selectedCompanyId",  companyId);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("selectedAccommodationId", accommodationId);
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("reviews", reviewPage.getDtoList());
        model.addAttribute("pageResponse", reviewPage);

        return "owner/review/list";
    }

    @GetMapping( "/review/room/{roomId}")
    public String reviews(@PathVariable(required = false) Long roomId,
                          @RequestParam(required = false) Long accommodationId,
                          @RequestParam(required = false) Long companyId,
                          @ModelAttribute PageRequestDTO pageRequestDTO,
                          Model model) {

        // 모든 숙소 조회 (숙소 선택용)
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        // 리뷰 페이지 조회
        PageResponseDTO<ReviewDTO> reviewPage;

        if (roomId != null) {
            // 특정 숙소 리뷰 조회
            reviewPage = reviewService.getReviewsByRoomID(roomId, pageRequestDTO);
            model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationId));
        } else {
            // 전체 리뷰 조회
            reviewPage = reviewService.getReviewsByRoomID(roomId ,pageRequestDTO);
        }

        // 모델에 데이터 추가
        model.addAttribute("selectedRoomId",  roomId);
        model.addAttribute("reviews", reviewPage.getDtoList());
        model.addAttribute("pageResponse", reviewPage);

        return "owner/review/list";
    }
    @GetMapping("/review/{reviewId}/modify")
    public String modifyReview(@PathVariable Long reviewId, Model model) {
        model.addAttribute("reviewId", reviewId);
        model.addAttribute("review", reviewService.getReviewById(reviewId));
        model.addAttribute("rooms", roomService.getAllRooms());
        return "owner/review/modify";
    }

    @PostMapping("/owner/review/{reviewId}/reply")
    public String saveReply(@PathVariable Long reviewId,
                            @RequestParam String reply) {
        Long roomId = roomService.getRoomIdByReviewId(reviewId);
        reviewService.saveReply(reviewId, reply);
        return "redirect:/owner/review/" + roomId;
    }


}
