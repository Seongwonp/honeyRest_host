package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.type.AccommodationStatus;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.serviceOwner.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
@Controller("ownerReservationController")
@RequestMapping("/owner")
@RequiredArgsConstructor
@Log4j2
public class ReservationController {

    private final OCompanyService companyService;
    private final OAccommodationService accommodationService;
    private final ORoomService roomService;
    private final OReservationService reservationService;
    private final OUserService userService;
    /** 취소 요청 승인/거절은 업체 관리자와 같은 서비스(도메인 전이 + 환불)를 쓴다 */
    private final com.honeyrest.honeyrest_host.serviceAdmin.ReservationService adminReservationService;


    /**
     * 예약 목록 (취소·취소 요청 제외). 업체/숙소/객실 필터를 쿼리 파라미터로 받는다.
     * /reservation/company/{id}, /accommodation/{id}, /room/{id} 경로도 같은 화면으로 보낸다.
     */
    @GetMapping("/reservation/list")
    public String reservationList(@RequestParam(required = false) Integer companyId,
                                  @RequestParam(required = false) Long accommodationId,
                                  @RequestParam(required = false) Long roomId,
                                  @ModelAttribute PageRequestDTO pageRequestDTO,
                                  Model model) {
        PageResponseDTO<ReservationDTO> reservationPage;
        if (roomId != null && roomId > 0) {
            reservationPage = reservationService.getReservationsByRoomIdWithPage(roomId, pageRequestDTO);
        } else if (accommodationId != null && accommodationId > 0) {
            reservationPage = reservationService.getReservationsByAccommodationIdWithPageable(accommodationId, pageRequestDTO);
            model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationId));
        } else {
            reservationPage = reservationService.getReservationsByCompanyIdWithPageable(companyId, pageRequestDTO);
        }
        if (companyId != null && companyId > 0) {
            model.addAttribute("company", companyService.getCompany(companyId));
        }
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("companyId", companyId);
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("roomId", roomId);
        model.addAttribute("reservations", reservationPage.getDtoList());
        model.addAttribute("reservationPage", reservationPage);
        return "owner/reservation/list";
    }

    @GetMapping("/reservation/accommodations")
    public String reservationsLegacy() {
        return "redirect:/owner/reservation/list";
    }

    @GetMapping("/reservation/company/{companyId}")
    public String companyReservations(@PathVariable Integer companyId) {
        return "redirect:/owner/reservation/list?companyId=" + companyId;
    }

    @GetMapping("/reservation/accommodation/{accommodationId}")
    public String accommodationReservations(@PathVariable Long accommodationId) {
        return "redirect:/owner/reservation/list?accommodationId=" + accommodationId;
    }

    @GetMapping("/reservation/room/{roomId}")
    public String roomReservations(@PathVariable Long roomId) {
        return "redirect:/owner/reservation/list?roomId=" + roomId;
    }

    /** 취소 요청 목록 (승인 = 취소 확정 + 환불, 거절 = 예약 확정 유지) */
    @GetMapping("/reservation/inActive/list")
    public String cancelReservations(@ModelAttribute PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<ReservationDTO> reservationPage =
                reservationService.getCancelRequestReservationsByCompanyIdWithPageable(null, pageRequestDTO);
        model.addAttribute("reservations", reservationPage.getDtoList());
        model.addAttribute("reservationPage", reservationPage);
        return "owner/reservation/inActive";
    }

    /** 취소 요청 승인: 업체 관리자 화면과 같은 도메인 전이 + 환불 */
    @PostMapping("/reservation/{reservationId}/cancel-approve")
    public String approveCancel(@PathVariable Long reservationId, RedirectAttributes ra) {
        try {
            adminReservationService.approveCancelRequest(reservationId, null);
            ra.addFlashAttribute("success", "취소 요청을 승인했습니다. (예약 취소 및 환불 처리)");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/owner/reservation/inActive/list";
    }

    /** 취소 요청 거절: 예약을 확정 상태로 되돌린다 */
    @PostMapping("/reservation/{reservationId}/cancel-reject")
    public String rejectCancel(@PathVariable Long reservationId,
                               @RequestParam(required = false) String reason,
                               RedirectAttributes ra) {
        try {
            adminReservationService.rejectCancelRequest(reservationId, reason);
            ra.addFlashAttribute("success", "취소 요청을 거절했습니다.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/owner/reservation/inActive/list";
    }

    @GetMapping("/reservation/{reservationId}/modify")
    public String modifyReservations(@PathVariable Long reservationId, Model model) {
        model.addAttribute("reservationId", reservationId);
        model.addAttribute("reservation" , reservationService.getReservation(reservationId));
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "owner/reservation/modify";
    }

    /**
     * 예약 수정 저장. 실패 시 수정 화면으로 돌아가 사유를 보여준다.
     */
    @PostMapping("/reservation/modify")
    public String modifyReservation(@ModelAttribute ReservationDTO form, RedirectAttributes ra) {
        if (form.getReservationId() == null) {
            ra.addFlashAttribute("error", "예약 ID가 없습니다.");
            return "redirect:/owner/reservation/list";
        }
        try {
            reservationService.modifyReservation(form);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // ReservationConflictException 도 IllegalStateException 하위 타입이라 여기서 처리된다
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/owner/reservation/" + form.getReservationId() + "/modify";
        } catch (EntityNotFoundException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/owner/reservation/list";
        }
        ra.addFlashAttribute("success", "예약이 수정되었습니다.");
        return "redirect:/owner/reservation/list";
    }



    @GetMapping("/reservation/create")
    public String createReservationForm(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("users", userService.getBookableUsers());
        return "owner/reservation/create";
    }

    /**
     * 관리자 예약 등록. 금액은 서버가 객실 요금 × 박 수 - 할인으로 계산한다.
     */
    @PostMapping("/reservation/create")
    public String createReservation(@ModelAttribute ReservationDTO form, RedirectAttributes ra) {
        UserDTO user = form.getUserId() != null
                ? userService.getUser(form.getUserId())
                : userService.getUserByNameAndPhone(form.getGuestName(), form.getGuestPhone());
        if (user == null) {
            ra.addFlashAttribute("error", "예약할 회원을 찾을 수 없습니다.");
            return "redirect:/owner/reservation/create";
        }
        if (form.getRoomId() == null) {
            ra.addFlashAttribute("error", "객실을 선택하세요.");
            return "redirect:/owner/reservation/create";
        }
        RoomDTO room;
        try {
            room = roomService.getByRoomId(form.getRoomId());
        } catch (EntityNotFoundException e) {
            ra.addFlashAttribute("error", "객실 정보를 찾을 수 없습니다.");
            return "redirect:/owner/reservation/create";
        }
        if (form.getCheckInDate() == null || form.getCheckOutDate() == null
                || !form.getCheckInDate().isBefore(form.getCheckOutDate())) {
            ra.addFlashAttribute("error", "체크아웃 날짜는 체크인 날짜 이후여야 합니다.");
            return "redirect:/owner/reservation/create";
        }
        int guestCount = form.getGuestCount() == null ? 1 : form.getGuestCount();
        if (room.getMaxOccupancy() != null && guestCount > room.getMaxOccupancy()) {
            ra.addFlashAttribute("error", "투숙 인원이 최대 수용 인원(" + room.getMaxOccupancy() + "명)을 초과합니다.");
            return "redirect:/owner/reservation/create";
        }

        long nights = java.time.temporal.ChronoUnit.DAYS.between(form.getCheckInDate(), form.getCheckOutDate());
        java.math.BigDecimal original = (room.getPrice() == null ? java.math.BigDecimal.ZERO : room.getPrice())
                .multiply(java.math.BigDecimal.valueOf(nights));
        java.math.BigDecimal discount = form.getDiscountAmount() == null ? java.math.BigDecimal.ZERO
                : form.getDiscountAmount().max(java.math.BigDecimal.ZERO).min(original);
        String number = (form.getReservationNumber() == null || form.getReservationNumber().isBlank())
                ? "HR-ADM-" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                  + "-" + (int) (Math.random() * 900 + 100)
                : form.getReservationNumber().trim();

        ReservationDTO reservation = ReservationDTO.builder()
                .userId(user.getUserId())
                .accommodationId(room.getAccommodationId())
                .roomId(room.getRoomId())
                .accommodationName(room.getAccommodationName())
                .roomName(room.getName())
                .guestName(form.getGuestName() == null || form.getGuestName().isBlank() ? user.getName() : form.getGuestName())
                .guestPhone(form.getGuestPhone() == null || form.getGuestPhone().isBlank() ? user.getPhone() : form.getGuestPhone())
                .guestCount(guestCount)
                .checkInDate(form.getCheckInDate())
                .checkOutDate(form.getCheckOutDate())
                .originalPrice(original)
                .discountAmount(discount)
                .price(original.subtract(discount))
                .reservationNumber(number)
                .status(form.getStatus())
                .specialRequest(form.getSpecialRequest())
                .build();

        try {
            reservationService.registerReservation(reservation);
        } catch (com.honeyrest.honeyrest_host.serviceCommon.ReservationConflictException | IllegalArgumentException e) {
            // 재고 부족 또는 잘못된 입력: 등록 화면으로 돌아가 사유를 보여준다
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/owner/reservation/create";
        }
        ra.addFlashAttribute("success", "예약을 등록했습니다. (예약번호 " + number + ")");
        return "redirect:/owner/reservation/list";
    }
//    @GetMapping("/userName/search")
//    @ResponseBody
//    public List<UserDTO> searchUser(@RequestParam String name) {
//        return userService.searchByNameContaining(name);
//    }
//    @GetMapping("/userPhone/search")
//    @ResponseBody
//    public List<UserDTO> searchUserBy(@RequestParam String name) {
//        return userService.searchByPhoneContaining(name);
//    }

    @GetMapping("/accommodation/search")
    @ResponseBody
    public List<AccommodationDTO> searchAccommodations(@RequestParam String keyword) {
        return accommodationService.searchByNameContaining(keyword).stream().filter(c -> AccommodationStatus.isActive(c.getStatus())).toList();
    }

}
