package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dtoAdmin.*;
import com.honeyrest.honeyrest_host.serviceAdmin.*;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Log4j2
@Controller("adminReservationController")
@RequiredArgsConstructor
@RequestMapping("/admin/reservations")

public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final CompanyService companyService;
    private final AccommodationService accommodationService;
    private final RoomService roomService;


    /**
     * 예약 목록 페이지 + 조회 -> 예약 취소 요청 목록으로 변경함
     */
    @GetMapping("/cancel-requests")
    public String cancelRequests(@RequestParam(required = false) String q,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {

        Integer companyId = companyService.getCompanyIdByOfCurrentUser();

        PageRequestDTO pr = PageRequestDTO.builder().page(page).size(size).build();
        PageResponseDTO<ReservationDTO> resp = reservationService.getCancelRequestsForCompany(companyId, q, pr);

        model.addAttribute("list", resp.getDtoList());
        model.addAttribute("total", resp.getTotal());
        model.addAttribute("currentPage", pr.getPage());
        model.addAttribute("pageSize", pr.getSize());
        model.addAttribute("totalPages", (int)Math.ceil((double)resp.getTotal() / pr.getSize()));
        model.addAttribute("q", q == null ? "" : q.trim());

        return "admin/reservations/cancel-requests";
    }

    /**
     * 취소승인 (사유 포함)
     */
    @PostMapping(value = "/cancel-request/{id}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> approve(@PathVariable Long id) {
        try {
            // 사유 없이 즉시 승인 (서비스 시그니처에 맞춰 호출)
            ReservationDTO updated = reservationService.approveCancelRequest(id, null);

            // 상태를 컨펌(예: CONFIRM/CONFIRMED 등)으로 바꾸는 로직은 서비스에 있어야 함
            return ResponseEntity.ok(Map.of(
                    "ok", true,
                    "status", updated != null ? updated.getStatus() : "CONFIRM"
            ));
        } catch (IllegalStateException e) {
            // 상태 충돌 등 비즈니스 예외
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.warn("취소승인 실패 id={}, err={}", id, e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "처리에 실패했습니다."));
        }
    }

    /**
     * 취소거부 (사유 포함)
     */
    @PostMapping("/cancel-request/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(required = false) String reason,
                         RedirectAttributes rttr) {
        try {
            // 예약 상태는 변경하지 않고(서비스도 상태 변경하지 않게!)
            reservationService.rejectCancelRequest(id, reason);
            rttr.addFlashAttribute("msg", "취소요청을 거부했습니다.");
        } catch (Exception e) {
            log.warn("취소거부 실패 id={}, err={}", id, e.getMessage());
            rttr.addFlashAttribute("msg", "취소거부에 실패했습니다: " + e.getMessage());
        }
        // ★ 목록 URL을 실제 매핑과 동일하게 복수형으로
        return "redirect:/admin/reservations/cancel-requests";
    }


    @PostMapping("/admin/reservations/{id}/complete")
    public String complete(@PathVariable Long id, RedirectAttributes ra) {
        reservationService.markCompleted(id);
        ra.addFlashAttribute("msg","체크아웃 완료 처리");
        return "redirect:/admin/reservations/list-all";
    }

    @PostMapping("/admin/reservations/{id}/no-show")
    public String noShow(@PathVariable Long id, RedirectAttributes ra) {
        reservationService.markNoShow(id);
        ra.addFlashAttribute("msg","노쇼 처리 완료");
        return "redirect:/admin/reservations/list-all";
    }




    /**
     * 내 예약 현황(회사 관리자용)
     */
    @GetMapping("/my")
    public String my(@RequestParam(defaultValue = "ALL") String status,
                     @RequestParam(required = false) String q,
                     @RequestParam(required = false) Long accId,
                     @RequestParam(defaultValue = "1") int page,
                     @RequestParam(defaultValue = "10") int size,
                     Authentication authentication,
                     Model model) {

        Integer companyId = companyService.getCompanyIdByOfCurrentUser();

        // 정렬 숙소명 -> 체크인
        Sort sort = Sort.by("accommodationName").descending()
                .and(Sort.by("checkInDate").ascending());

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, sort);

        // 서비스에 getCompanyReservations(companyId, status, q, pr) 구현 필요
        Page<ReservationDTO> resp =
                reservationService.getCompanyReservations(companyId, status, q, accId, pageable);

        int currentPage = page;
        int pageSize = size;
        int total = (int) resp.getTotalElements();
        int totalPages = resp.getTotalPages();

        int blockSize = 5;
        int startPage = ((currentPage - 1) / blockSize) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPages);
        boolean hasPrevBlock = startPage > 1;
        boolean hasNextBlock = endPage < totalPages;


        // 뷰 모델
        model.addAttribute("list", resp.getContent());
        model.addAttribute("total", total);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasPrevBlock", hasPrevBlock);
        model.addAttribute("hasNextBlock", hasNextBlock);

        model.addAttribute("statuses", List.of("CONFIRMED", "PENDING", "COMPLETED", "CANCEL_REQUEST", "NO_SHOW"));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("q", q == null ? "" : q);

        model.addAttribute("accOptions", accommodationService.getAllById(companyId));
        model.addAttribute("selectedAccId", accId);

        return "admin/reservations/my";
    }

    /**
     * 환불/취소 관리
     */
    @GetMapping("/refunds")
    public String refunds() {
        return "redirect:/admin/point/refunds";
    }

    /**
     * 신규 예약 폼
     */
    @GetMapping("/new")
    public String newReservation(Authentication authentication, Model model) {

        Integer companyId = companyService.getCompanyIdByOfCurrentUser();

        ReservationDTO form = new ReservationDTO();
        model.addAttribute("form", form);

        // 드롭다운 데이터(회사 기준)
        model.addAttribute("accomodations", accommodationService.getAllById(companyId));
        model.addAttribute("rooms", roomService.findAllByCompanyId(companyId));


        return "admin/reservations/new";
    }

    /**
     * 예약 생성 (POST)
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("form") ReservationDTO form,
                         RedirectAttributes ra) {
        ReservationDTO saved = reservationService.createReservation(form);
        ra.addFlashAttribute("msg", "예약이 등록되었습니다. (" + saved.getReservationNumber() + ")");
        return "redirect:/admin/reservations/list?number=" + saved.getReservationNumber();
    }

    /**
     * 예약 상세 페이지
     */
    @GetMapping("/{reservationId}")
    public String detail(@PathVariable Long reservationId, Model model) {
        ReservationDTO reservation = reservationService.getReservationDetail(reservationId);
        model.addAttribute("reservation", reservation); // DTO로 바꾸고 싶으면 매핑해서
        return "admin/reservations/detail";
    }

    /**
     * 예약 취소 – 재고 복구는 서비스에서 처리
     */
    @PostMapping("/{reservationId}/cancel")
    public String cancel(@PathVariable Long reservationId,
                         @RequestParam(required = false) String reason,
                         RedirectAttributes ra) {
        reservationService.cancelReservation(reservationId, reason);
        ra.addFlashAttribute("msg", "예약이 취소되었습니다.");
        return "redirect:/admin/reservations/list";
    }

    @GetMapping("/day")
    public String day(@RequestParam Integer companyId,
                      @RequestParam(required = false) Long accommodationId,
                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                      Model model) {

        // 날짜 파라미터가 없으면 오늘로 기본값
        if (date == null) {
            date = LocalDate.now();
        }

        // 상단 숙소 필터 셀렉트용: 해당 회사의 숙소 목록
        model.addAttribute("accommodations", accommodationService.getAllById(companyId));

        // 목록 데이터: 회사(+선택 숙소)의 '해당 날짜에 걸치는' 예약만
        List<ReservationDTO> list =
                reservationService.findCompanyReservationsOnDate(companyId, accommodationId, date);

        // 템플릿에서 쓰는 모델값들
        model.addAttribute("companyId", companyId);
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("date", date);
        model.addAttribute("list", list);

        return "admin/reservations/day";
    }

}