package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.*;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.service.*;
import com.honeyrest.honeyrest_host.service.accommodation.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reservations")

public class ReservationPageController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final CompanyService companyService;
    private final AccommodationService accommodationService;
    private final RoomService roomService;


    /** 예약 목록 페이지 + 조회 */
    @GetMapping("/list")
    public String list(@RequestParam(required = false) String number,
                       @RequestParam(defaultValue = "ALL") String status,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {


        model.addAttribute("reservationStatuses", List.of("CONFIRMED", "PENDING", "COMPLETED", "CANCELLED", "NO_SHOW"));

        PageRequestDTO pr = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .build();

        PageResponseDTO<ReservationDTO> resp;

        if (number != null && !number.isBlank()) {
            try {
                ReservationDTO dto = reservationService.getReservationByNumber(number.trim());
                resp = PageResponseDTO.<ReservationDTO>withALl()
                        .pageRequestDTO(pr)
                        .dtoList(List.of(dto))
                        .total(1)
                        .build();
                model.addAttribute("msg", null);
            } catch (Exception e) {
                resp = PageResponseDTO.<ReservationDTO>withALl()
                        .pageRequestDTO(pr)
                        .dtoList(List.of())
                        .total(0)
                        .build();
                model.addAttribute("msg", "해당 예약번호를 찾을 수 없습니다.");
            }
        } else {
            resp = reservationService.getReservationsByStatus(status, pr);
        }

        // ✅ 템플릿에서 쓰기 쉬운 평면 값들을 모델에 담아줌
        int currentPage = pr.getPage();
        int pageSize = pr.getSize();
        int total = resp.getTotal();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        model.addAttribute("list", resp.getDtoList());
        model.addAttribute("total", total);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("selectedStatus", status);
        model.addAttribute("number", number == null ? "" : number.trim());

        return "admin/reservations/list";
    }


    /** 내 예약 현황(회사 관리자용) */
    @GetMapping("/my")
    public String my(@RequestParam(defaultValue = "ALL") String status,
                     @RequestParam(required = false) String q,
                     @RequestParam(defaultValue = "1") int page,
                     @RequestParam(defaultValue = "10") int size,
                     org.springframework.security.core.Authentication authentication,
                     Model model) {

        Long companyId = resolveCompanyId(authentication); // 프로젝트 정책에 맞게 구현
        PageRequestDTO pr = PageRequestDTO.builder().page(page).size(size).build();

        // 서비스에 getCompanyReservations(companyId, status, q, pr) 구현 필요
        PageResponseDTO<ReservationDTO> resp = reservationService.getCompanyReservations(companyId, status, q, pr);

        int currentPage = pr.getPage();
        int pageSize = pr.getSize();
        int total = resp.getTotal();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        model.addAttribute("list", resp.getDtoList());
        model.addAttribute("total", total);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("statuses",List.of("CONFIRMED", "PENDING", "COMPLETED", "CANCELLED"));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("q", q == null ? "" : q);

        return "admin/reservations/my";
    }

    /** 환불/취소 관리 */
    @GetMapping("/refunds")
    public String refunds(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          Model model) {
        // 필요 시 환불 대상만 필터링해서 조회
        PageRequestDTO pr = PageRequestDTO.builder().page(page).size(size).build();
        PageResponseDTO<ReservationDTO> resp =
                reservationService.getReservationsByStatus("CANCELLED", pr);

        model.addAttribute("list", resp.getDtoList());
        model.addAttribute("total", resp.getTotal());
        model.addAttribute("currentPage", pr.getPage());
        model.addAttribute("pageSize", pr.getSize());
        model.addAttribute("totalPages", (int)Math.ceil((double)resp.getTotal()/pr.getSize()));

        return "admin/reservations/refunds";
    }

    /** 신규 예약 폼 */
    @GetMapping("/new")
    public String newReservation(Authentication authentication, Model model) {

        String email = (authentication.getPrincipal() instanceof String s) ? s : authentication.getName();

        AdminLoginRequestDTO admin = userService.getUserByEmail(email);
        CompanyDTO companyDTO = companyService.getByUserEmail(admin.getEmail());
        Long companyId = companyDTO.getCompanyId();
        AccommodationCreateRequestDTO accDto = accommodationService.getById(companyId);
        Long accommodationId = accDto.getAccommodationId();
        RoomDTO roomDTO = roomService.getByRoomId(accommodationId);
        Long roomId = roomDTO.getRoomId();
        ReservationDTO form = new ReservationDTO();
        model.addAttribute("form", form);
        model.addAttribute("roomId", roomId); // 위에 꺼를 html 로 넘기기 위해 model 로 넘겨주어야 함.
        model.addAttribute("accommodations", accommodationService.getAllById(companyId));
        model.addAttribute("rooms", roomService.findAllByCompanyId(companyId));


        return "admin/reservations/new";
    }

    /** 예약 생성 (POST) */
    @PostMapping
    public String create(@Valid @ModelAttribute("form") ReservationDTO form,
                         RedirectAttributes ra) {
        ReservationDTO saved = reservationService.createReservation(form);
        ra.addFlashAttribute("msg", "예약이 등록되었습니다. (" + saved.getReservationNumber() + ")");
        return "redirect:/admin/reservations/list?number=" + saved.getReservationNumber();
    }

    /** 예약 상세 페이지 */
    @GetMapping("/{reservationId}")
    public String detail(@PathVariable Long reservationId, Model model) {
        var entity = reservationService.getReservationById(reservationId);
        model.addAttribute("reservation", entity); // DTO로 바꾸고 싶으면 매핑해서
        return "admin/reservations/detail";
    }

    /** 예약 취소 – 재고 복구는 서비스에서 처리 */
    @PostMapping("/{reservationId}/cancel")
    public String cancel(@PathVariable Long reservationId,
                         @RequestParam(required = false) String reason,
                         RedirectAttributes ra) {
        reservationService.canceledReservation(reservationId, reason);
        ra.addFlashAttribute("msg", "예약이 취소되었습니다.");
        return "redirect:/admin/reservations/list";
    }
    /* ========== 헬퍼 ========== */
    private Long resolveCompanyId(org.springframework.security.core.Authentication authentication) {
        // 예시) principal에 userId(Long) 저장되어 있고, UserRepository 통해 companyId 조회
        // return companyService.findCompanyIdByUserId((Long) authentication.getPrincipal());
        return 1L; // 임시
    }

}