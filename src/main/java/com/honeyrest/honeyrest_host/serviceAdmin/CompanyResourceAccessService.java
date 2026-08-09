package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.ReservationDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * 회사 관리자 요청에서 URL/form의 리소스 ID가 로그인 회사 소유인지 확인한다.
 */
@Service
@RequiredArgsConstructor
public class CompanyResourceAccessService {

    private final CompanyService companyService;
    private final AccommodationService accommodationService;
    private final RoomService roomService;
    private final ReservationService reservationService;

    public Integer currentCompanyId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        CompanyDTO company = companyService.getByUserEmail(authentication.getName());
        return company == null ? null : company.getCompanyId();
    }

    public boolean ownsAccommodation(Integer companyId, Long accommodationId) {
        if (companyId == null || accommodationId == null) return false;
        try {
            AccommodationCreateRequestDTO accommodation = accommodationService.getById(accommodationId);
            return accommodation != null && companyId.equals(accommodation.getCompanyId());
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    public boolean ownsRoom(Integer companyId, Long roomId) {
        if (companyId == null || roomId == null) return false;
        try {
            RoomDTO room = roomService.getByRoomId(roomId);
            return room != null && ownsAccommodation(companyId, room.getAccommodationId());
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    public boolean ownsReservation(Integer companyId, Long reservationId) {
        if (companyId == null || reservationId == null) return false;
        try {
            ReservationDTO reservation = reservationService.getReservationDetail(reservationId);
            return reservation != null && ownsAccommodation(companyId, reservation.getAccommodationId());
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    public boolean canCreateReservation(Integer companyId, ReservationDTO form) {
        if (form == null || !ownsRoom(companyId, form.getRoomId())) return false;
        RoomDTO room = roomService.getByRoomId(form.getRoomId());
        Long roomAccommodationId = room.getAccommodationId();
        return form.getAccommodationId() == null || form.getAccommodationId().equals(roomAccommodationId);
    }
}
