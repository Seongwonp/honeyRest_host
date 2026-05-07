package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dtoAdmin.*;
import com.honeyrest.honeyrest_host.serviceAdmin.ReservationService;
import com.honeyrest.honeyrest_host.serviceAdmin.ReviewImageService;
import com.honeyrest.honeyrest_host.serviceAdmin.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;


//@Profile({"local", "dev"}) // 운영(prod)에서는 자동 비활성
@Controller("adminReviewController")
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewImageService reviewImageService;
    private final ReservationService reservationService;

    /**
     * 공백 -> null 치환
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /* 리뷰 목록 */
    @GetMapping("/list")
    public String list(@RequestParam(required = false) String status,
                       @RequestParam(required = false) Long roomId,
                       @RequestParam(required = false) Long accommodationId,
                       @RequestParam(defaultValue = "list") String view,
                       @RequestParam(defaultValue = "latest") String sort,
                       @ModelAttribute PageRequestDTO pageRequestDTO,
                       Model model) {

        PageResponseDTO<ReviewDTO> page =
                reviewService.getList(status, roomId, accommodationId, sort, pageRequestDTO);

        // 화면에 그려진 리뷰들 id 모으기
        //여러 리뷰 이미지 한번에 조회하기


        // 썸네일(첫장)과 개수 맵
        Map<Long, String> thumbUrlMap = new HashMap<>();
        Map<Long, Integer> imageCountMap = new HashMap<>();

        List<Long> reviewIds = page.getDtoList().stream()
                .map(ReviewDTO::getReviewId)
                .toList();

        if (!reviewIds.isEmpty()) {
            Map<Long, List<ReviewImageDTO>> imagesByReviewId =
                    reviewImageService.getImagesForReviews(reviewIds);

            for (Long rid : reviewIds) {
                List<ReviewImageDTO> list = imagesByReviewId.getOrDefault(rid, Collections.emptyList());
                if (!list.isEmpty()) {
                    thumbUrlMap.put(rid, list.get(0).getImageUrl()); // 첫 화면
                }
                imageCountMap.put(rid, list.size()); // 개수 채우기
            }
        }


        model.addAttribute("view", view);
        model.addAttribute("page", page); // 전체 페이지 객체
        model.addAttribute("roomId", roomId);
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("list", page.getDtoList());
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);

        // 목록 뷰에서 사용하기 위함
        model.addAttribute("thumbUrlMap", thumbUrlMap);
        model.addAttribute("imageCountMap", imageCountMap);
        return "admin/reviews/list";
    }


    /* 상세*/
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return reviewService.getOne(id).map(dto -> {
                    List<ReviewImageDTO> imgs = reviewImageService.getImages(id);
                    dto.setImageList(imgs);

                    model.addAttribute("review", dto);
                    return "admin/reviews/detail";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "리뷰를 찾을 수 없습니다.");
                    return "redirect:/admin/reviews/list";
                });
    }

    /**
     * 상세에서 상태/답변 저장 (부분 저장)
     */
    @PostMapping("/detail/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("review") ReviewDTO form,
                         BindingResult bindingResult,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "admin/reviews/detail";
        }
        ReviewDTO saved = reviewService.patch(id, form);
        ra.addFlashAttribute("success", "리뷰가 수정(저장)되었습니다. (ID: " + saved.getReviewId() + ")");
        return "redirect:/admin/reviews/detail/{id}";
    }

    /**
     * 상세에서 이미지 업로드
     */
    @PostMapping("/detail/{id}/images")
    public String uploadImages(@PathVariable Long id,
                               @RequestParam("images") List<MultipartFile> files,
                               RedirectAttributes ra) {
        if (files != null && !files.isEmpty()) {
            reviewImageService.uploadImages(id, files); // 서비스에서 Firebase 업로드 + DB 저장
            ra.addFlashAttribute("success", "이미지를 업로드했습니다.");
        } else {
            ra.addFlashAttribute("error", "업로드할 이미지가 없습니다.");
        }
        return "redirect:/admin/reviews/detail/{id}";
    }

    /**
     * 상세에서 이미지 삭제
     */
    @PostMapping("/detail/{reviewId}/images/{imageId}/delete")
    public String deleteImage(@PathVariable Long reviewId,
                              @PathVariable Long imageId,
                              RedirectAttributes ra) {
        reviewImageService.deleteImage(reviewId, imageId, true); // 서비스 내부에서 Firebase 삭제 + DB 삭제
        ra.addFlashAttribute("success", "이미지를 삭제했습니다.");
        return "redirect:/admin/reviews/detail/{reviewId}";
    }

    /* ===================== 등록 ===================== */

    /* 등록 폼 */
    @GetMapping("/add")
    public String addForm(@RequestParam(required = false) Long reservationId, Model model, RedirectAttributes ra) {
        ReviewDTO form = new ReviewDTO();
        form.setStatus("VISIBLE"); // 기본값 예시

        if (reservationId != null) {
            ReservationDTO r = reservationService.getReservationById(reservationId);
            if (r != null) {
                form.setReservationId(r.getReservationId());
                form.setAccommodationId(r.getAccommodationId());
                form.setRoomId(r.getRoomId());
                form.setUserId(r.getUserId());

                String guest =
                        (r.getGuestName() != null && !r.getGuestName().isBlank())
                                ? r.getGuestName()
                                : r.getUserName();

                //화면 상단에 보여줄 프리뷰 용
                model.addAttribute("accName", r.getAccommodationName());
                model.addAttribute("roomName", r.getRoomName());
                model.addAttribute("guestName", guest);
            } else {
                // 예약 못 찾은 경우
                ra.addFlashAttribute("error", "해당 예약 ID를 찾을 수 없습니다. 다시 시도해주세요.");
                return "redirect:/admin/reviews/add"; // 다시 add 페이지로 리다이렉트
            }
        }
        model.addAttribute("form", form);
        return "admin/reviews/add";
    }

    /* 등록 처리 */
    @PostMapping("/add")
    public String addSubmit(@Valid @ModelAttribute("form") ReviewDTO form,
                            BindingResult binding,
                            @RequestParam(value = "files", required = false) List<MultipartFile> files, // 업로드 파일
                            RedirectAttributes ra) {

        if (binding.hasErrors()) {
            return "admin/reviews/add";
        }

        // 리뷰 저장
        ReviewDTO saved = reviewService.insert(form);

        // 이미지 업로드(폼 name="uploadFiles")
        if (files != null && !files.isEmpty()) {
            reviewImageService.uploadImages(saved.getReviewId(), files);
        }

        ra.addFlashAttribute("success", "리뷰가 등록되었습니다. (ID: " + saved.getReviewId() + ")");
        return "redirect:/admin/reviews/detail/" + saved.getReviewId(); // ★ 슬래시 누락 수정
    }


    /* ===================== 수정 ===================== */

    /**
     * 수정 폼
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id) {
        return "redirect:/admin/reviews/detail/" + id;
    }

//    /** 수정 저장(전체 갱신) */
//    @PostMapping("/{id}")
//    public String update(@PathVariable Long id,
//                         @Valid @ModelAttribute("form") ReviewDTO form,
//                         RedirectAttributes ra) {
//        // 부분 갱신이므로 patch 사용 (null 필드는 그대로 유지)
//        ReviewDTO saved = reviewService.patch(id, form);
//        ra.addFlashAttribute("success", "리뷰가 수정되었습니다. (ID: " + saved.getReviewId() + ")");
//        return "redirect:/admin/reviews/{id}";
//    }


    /* ===================== 부분 수정(답변/상태 등) ===================== */

    /**
     * 답변 등록/수정만(부분 업데이트)
     */
    @PostMapping("/{id}/reply")
    public String saveReply(@PathVariable Long id,
                            @RequestParam String reply,
                            RedirectAttributes ra) {
        ReviewDTO patch = ReviewDTO.builder().reply(reply).build();
        reviewService.patch(id, patch);
        ra.addFlashAttribute("success", "답변이 저장되었습니다.");
        return "redirect:/admin/reviews/detail/" + id;
    }

    /**
     * 상태 변경 (VISIBLE/HIDDEN 등)
     */
    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam String value,
                               RedirectAttributes ra) {
        try {
            reviewService.changeStatus(id, value);
            ra.addFlashAttribute("success", "상태를 " + value + "(으)로 변경했습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "상태 변경 실패: " + e.getMessage());
        }
        return "redirect:/admin/reviews/list";
    }




    /* ===================== 삭제(소프트) ===================== */


    /* 소프트 삭제 → HIDDEN */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @RequestParam(required = false) String status,
                         @RequestParam(required = false) String sort,
                         @RequestParam(required = false, defaultValue = "1") int page,
                         @RequestParam(required = false, defaultValue = "10") int size,
                         @RequestParam(required = false, defaultValue = "list") String view,
                         RedirectAttributes ra) {
        log.info(">> DELETE request for review {}", id);
        try {
            // 서비스에 위임 (내부에서 softHide 처리)
            reviewService.delete(id);

            ra.addFlashAttribute("success", "리뷰를 삭제(숨김)했습니다.");
        } catch (jakarta.persistence.EntityNotFoundException e) {
            ra.addFlashAttribute("error", "삭제할 리뷰를 찾지 못했습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "삭제 중 오류가 발생했습니다.");
        }

        // 현재 보던 리스트 상태를 그대로 유지해서 리다이렉트
        ra.addAttribute("status", status);
        ra.addAttribute("sort", sort);
        ra.addAttribute("page", page);
        ra.addAttribute("size", size);
        ra.addAttribute("view", view);

        return "redirect:/admin/reviews/list";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id,
                         @RequestParam(required = false) String view,
                         @RequestParam(required = false) String status,
                         @RequestParam(required = false) String sort,
                         @RequestParam(required = false, defaultValue = "1") Integer page,
                         @RequestParam(required = false, defaultValue = "10") Integer size,
                         RedirectAttributes ra) {
        ReviewDTO after = reviewService.toggleVisibleHidden(id);
        ra.addFlashAttribute("success", "상태를 " + after.getStatus() + "(으)로 변경했습니다.");
        // 현재 필터/뷰 유지한 채 목록으로
        return "redirect:/admin/reviews/list"
               + "?view=" + (view != null ? view : "list")
               + (status != null ? "&status=" + status : "")
               + (sort != null ? "&sort=" + sort : "")
               + "&page=" + page + "&size=" + size;
    }

}
