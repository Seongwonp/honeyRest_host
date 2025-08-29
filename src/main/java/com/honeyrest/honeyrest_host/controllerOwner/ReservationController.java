package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class ReservationController {

    private final CompanyService companyService;
    private final AccommodationService accommodationService;
    private final RoomService roomService;
    private final CouponService couponService;
    private final ReservationService reservationService;

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
            reservationPage = reservationService.getReservationsByCompanyIdWithPageable(accommodationId, pageRequestDTO);
            AccommodationDTO accommodation = accommodationService.getByAccommodationId(accommodationId);

            model.addAttribute("reservation", reservationPage.getDtoList());
            model.addAttribute("accommodation", accommodation);
        } else {
            reservationPage = reservationService.getReservationsByCompanyIdWithPageable(accommodationId, pageRequestDTO);

        }
        model.addAttribute("reservationPage", reservationPage);
        model.addAttribute("reservations", reservationPage.getDtoList());

        model.addAttribute("companies",  companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());

        model.addAttribute("selectedCompanyId",  companyId);
        model.addAttribute("selectedAccommodationId", accommodationId);

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
        model.addAttribute("selectedCompanyId", companyId);
        model.addAttribute("selectedAccommodationId", accommodationId);

        model.addAttribute("reservations", reservationPage.getDtoList());
        model.addAttribute("companies", companies);
        model.addAttribute("reservationPage", reservationPage);
        return "/owner/reservation/list";
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
    public String createRoom(@ModelAttribute RoomDTO roomDTO) {
        roomService.registerRoom(roomDTO);
        return "redirect:/owner/reservation/list";
    }
}
