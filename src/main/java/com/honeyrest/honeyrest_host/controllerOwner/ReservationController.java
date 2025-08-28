package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        return "/owner/reservation/accommodation";
    }

    @GetMapping("/reservation/accommodation/{accommodationId}")
    public String accommodationReservations(@PathVariable Long accommodationId, Model model) {
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();
        List<ReservationDTO> reservations;
        if (accommodationId != null) {
            reservations = reservationService.getReservationsByAccommodationId(accommodationId);
            AccommodationDTO accommodation = accommodationService.getByAccommodationId(accommodationId);
            model.addAttribute("reservation", reservations);
            model.addAttribute("accommodation", accommodation);
        } else {
            reservations = reservationService.getReservations();
        }
        model.addAttribute("reservations", reservations);
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("accommodationId", accommodationId);
        return "/owner/reservation/accommodation";
    }

    @GetMapping({ "/reservation/companies","/reservation/company/{companyId}"})
    public String companyReservations(
            @PathVariable(required = false) Long companyId,
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
        model.addAttribute("reservations", reservationPage.getDtoList());
        model.addAttribute("companies", companies);
        model.addAttribute("reservationPage", reservationPage);
        return "/owner/reservation/company";
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
