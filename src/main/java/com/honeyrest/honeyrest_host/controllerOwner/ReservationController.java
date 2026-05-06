package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.serviceOwner.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/reservation/accommodations")
    public String reservations(Model model) {
        model.addAttribute("accommodationId", 0);
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("reservations", reservationService.getReservations());
        return "/owner/reservation/list";
    }

    @GetMapping("/reservation/accommodation/{accommodationId}")
    public String accommodationReservations(
            @PathVariable Long accommodationId,
            @ModelAttribute PageRequestDTO pageRequestDTO,
            Model model) {
        Long companyId = companyService.getCompanyIdByAccommodationId(accommodationId);

        PageResponseDTO<ReservationDTO> reservationPage;

        if (accommodationId != null) {
            reservationPage = reservationService.getReservationsByAccommodationIdWithPageable(accommodationId, pageRequestDTO);
            AccommodationDTO accommodation = accommodationService.getByAccommodationId(accommodationId);

            model.addAttribute("reservation", reservationPage.getDtoList());
            model.addAttribute("accommodation", accommodation);
        } else {
            reservationPage = reservationService.getReservationsByAccommodationIdWithPageable(accommodationId, pageRequestDTO);

        }
        model.addAttribute("reservationPage", reservationPage);
        model.addAttribute("reservations", reservationPage.getDtoList());

        model.addAttribute("companies",  companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());

        model.addAttribute("companyId",  companyId);
        model.addAttribute("accommodationId", accommodationId);

        return "/owner/reservation/list";
    }

    @GetMapping({ "/reservation/list","/reservation/company/{companyId}"})
    public String companyReservations(
            @PathVariable(required = false) Long companyId,
            @RequestParam(required = false) Long accommodationId,
            @ModelAttribute PageRequestDTO pageRequestDTO,
            Model model) {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        PageResponseDTO<ReservationDTO> reservationPage;
        if (companyId != null) {
            reservationPage = reservationService.getReservationsByCompanyIdWithPageable(companyId, pageRequestDTO);
            CompanyDTO company = companyService.getCompany(companyId);
            model.addAttribute("reservation", reservationPage.getDtoList());
            model.addAttribute("company", company);
            model.addAttribute("companyId", companyId);
        } else {
            reservationPage = reservationService.getReservationsByCompanyIdWithPageable(companyId, pageRequestDTO);
            model.addAttribute("companyId", 0);
        }

        // 회사/숙소 리스트
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        // 선택값 유지용
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("companyId", companyId);
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("rooms", roomService.getAllRooms());

        model.addAttribute("reservations", reservationPage.getDtoList());
        model.addAttribute("companies", companies);
        model.addAttribute("reservationPage", reservationPage);
        return "/owner/reservation/list";
    }

    @GetMapping("/reservation/inActive/list")
    public String cancelReservations(
                                     @ModelAttribute PageRequestDTO pageRequestDTO,
                                     Model model) {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        PageResponseDTO<ReservationDTO> reservationPage =
                reservationService.getCancelRequestReservationsByCompanyIdWithPageable(null, pageRequestDTO);
        model.addAttribute("reservation", reservationPage.getDtoList());
        model.addAttribute("companyId", 0);


        // 회사/숙소 리스트
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();

        // 선택값 유지용
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("rooms", roomService.getAllRooms());

        model.addAttribute("reservations", reservationPage.getDtoList());
        model.addAttribute("companies", companies);
        model.addAttribute("reservationPage", reservationPage);
        return "/owner/reservation/inActive";
    }

    @GetMapping("/reservation/room/{roomId}")
    public String roomReservations(@PathVariable Long roomId,
                                   @RequestParam(required = false) Long companyId,
                                   @RequestParam(required = false) Long accommodationId,
                                   @ModelAttribute PageRequestDTO pageRequestDTO,
                                   Model model){
        List<RoomDTO> rooms = roomService.getAllRooms();
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();
        List<CompanyDTO> companies = companyService.getAllCompanies();

        model.addAttribute("rooms", rooms);
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("companies", companies);
        model.addAttribute("companyId", companyId);
        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("roomId", roomId);

        PageResponseDTO<ReservationDTO> reservationPage = reservationService.getReservationsByRoomIdWithPage(roomId, pageRequestDTO);

        model.addAttribute("reservations", reservationPage.getDtoList());
        model.addAttribute("reservationPage", reservationPage);

        return "owner/reservation/list";
    }

    @GetMapping("/reservation/{reservationId}/modify")
    public String modifyReservations(@PathVariable Long reservationId, Model model) {
        model.addAttribute("reservationId", reservationId);
        model.addAttribute("reservation" , reservationService.getReservation(reservationId));
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "owner/reservation/modify";
    }



    @GetMapping("/reservation/create")
    public String createRoom(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("companies", companyService.getAllCompanies());
        return "owner/reservation/create";
    }

    @PostMapping("/reservation/create")
    public String createReservation(@ModelAttribute ReservationDTO form) {
        UserDTO user = userService.getUserByNameAndPhone(form.getGuestName(), form.getGuestPhone());
        AccommodationDTO accommodation = accommodationService.getByName(form.getAccommodationName());

        RoomDTO room = roomService.getByAccommodationIdAndId(accommodation.getAccommodationId(), Long.valueOf(form.getRoomName()));

        // 유효성 검사
        if (form.getGuestCount() > room.getMaxOccupancy()) {
            throw new IllegalArgumentException("투숙 인원이 초과되었습니다.");
        }

        ReservationDTO reservation = ReservationDTO.builder()
                .userId(user.getUserId())
                .accommodationId(accommodation.getAccommodationId())
                .roomId(room.getRoomId())
                .accommodationName(accommodation.getName())
                .roomName(room.getName())
                .guestName(form.getGuestName())
                .guestPhone(form.getGuestPhone())
                .guestCount(form.getGuestCount())
                .checkInDate(form.getCheckInDate())
                .checkOutDate(form.getCheckOutDate())
                .originalPrice(form.getOriginalPrice())
                .discountAmount(form.getDiscountAmount())
                .price(form.getPrice())
                .reservationNumber(form.getReservationNumber())
                .status(form.getStatus())
                .specialRequest(form.getSpecialRequest())
                .cancelReason(form.getCancelReason())
                .build();

        reservationService.registerReservation(reservation);
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
        return accommodationService.searchByNameContaining(keyword).stream().filter(c-> c.getStatus().equalsIgnoreCase("active")).toList();
    }

}
