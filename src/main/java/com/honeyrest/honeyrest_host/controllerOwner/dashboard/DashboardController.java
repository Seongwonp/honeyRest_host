package com.honeyrest.honeyrest_host.controllerOwner.dashboard;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.CouponDTO;
import com.honeyrest.honeyrest_host.dtoOwner.RoomDTO;
import com.honeyrest.honeyrest_host.entity.Company;
import com.honeyrest.honeyrest_host.service.AccommodationService;
import com.honeyrest.honeyrest_host.service.CompanyService;
import com.honeyrest.honeyrest_host.service.CouponService;
import com.honeyrest.honeyrest_host.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class DashboardController {
    private final CompanyService companyService;
    private final AccommodationService accommodationService;
    private final RoomService roomService;
    private final CouponService couponService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        model.addAttribute("rooms", roomService.getAllRooms());

        return "owner/dashboard/dashboard";
    }

    @GetMapping("/company/list")
    public String company(Model model) {
        model.addAttribute("companies", companyService.getAllCompanies());
        return "owner/company/list";
    }

    @GetMapping("/company/{companyId}/accommodations")
    public String accommodationsByCompany(@PathVariable Long companyId, Model model) {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        List<AccommodationDTO> accommodations;

        if (companyId > 0) {
            accommodations = accommodationService.getAccommodationsByCompanyId(companyId);
            model.addAttribute("company", companyService.getCompany(companyId));
        } else {
            accommodations = accommodationService.getAllAccommodations();
            model.addAttribute("companies", companies);
            model.addAttribute("accommodations", accommodations);
            return "owner/company/list";
        }

        model.addAttribute("companies", companies);
        model.addAttribute("accommodations", accommodations);

        return "owner/accommodation/list";
    }
    @GetMapping("/accommodation/list")
    public String accommodations(Model model) {
        model.addAttribute("accommodations", accommodationService.getAllAccommodations());
        return "owner/accommodation/list";
    }

    @GetMapping("/accommodation/{accommodationId}/rooms")
    public String roomsByAccommodation(@PathVariable Long accommodationId, Model model) {
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();
        List<CompanyDTO> companies = companyService.getAllCompanies();

        List<RoomDTO> roomDTOS;

        if (accommodationId > 0) {
            roomDTOS = roomService.getRoomsByAccommodationId(accommodationId);
            model.addAttribute("accommodation", accommodationService.getByAccommodationId(accommodationId));
        } else {
            roomDTOS = roomService.getAllRooms();
            model.addAttribute("accommodations", accommodations);
            model.addAttribute("companies", companies);
            model.addAttribute("rooms", roomDTOS);
            return "owner/accommodation/list";
        }
        model.addAttribute("accommodations", accommodations);
        model.addAttribute("rooms", roomDTOS);

        return "owner/room/list";
    }
    @GetMapping("/room/list")
    public String rooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        return "owner/room/list";
    }

    @GetMapping("/coupon/list")
    public String coupons(Model model) {
        model.addAttribute("coupons", couponService.getCoupons());
        return "owner/coupon/list";
    }

    @GetMapping("/coupon/create")
    public String createCoupon(Model model) {
        return "owner/coupon/create";
    }
    @PostMapping("/coupon/create")
    public String createCoupon(@ModelAttribute CouponDTO couponDTO) {
        couponService.registerCoupon(couponDTO);
        return "redirect:/owner/coupon/list";
    }
}
