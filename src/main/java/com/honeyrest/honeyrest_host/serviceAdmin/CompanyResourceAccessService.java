package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.InquiryDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.ReservationDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.ReviewDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.RoomDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * 회사 관리자 요청에서 URL/form의 리소스 ID가 로그인 회사 소유인지 확인한다.
 * <p>
 * 빈 이름을 {@value #BEAN_NAME} 로 고정해 컨트롤러에서 메서드 보안 SpEL 로 바로 쓴다.
 * <pre>
 * &#64;PreAuthorize("&#64;companyAccess.ownsAccommodation(#id, authentication)")
 * </pre>
 * 검사에 실패하면 AuthorizationDeniedException(AccessDeniedException 하위)이 나고
 * GlobalExceptionHandler 가 403(error/403) 화면으로 응답한다. 존재하지 않는 ID 도 false(403)로 처리해
 * 다른 회사 리소스의 존재 여부를 드러내지 않는다(fail-closed).
 */
@Service(CompanyResourceAccessService.BEAN_NAME)
@RequiredArgsConstructor
public class CompanyResourceAccessService {

    /** SpEL({@code @companyAccess})에서 참조하는 빈 이름. 바꾸면 모든 @PreAuthorize 식을 함께 바꿔야 한다. */
    public static final String BEAN_NAME = "companyAccess";

    private final CompanyService companyService;
    private final AccommodationService accommodationService;
    private final RoomService roomService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;
    private final InquiryService inquiryService;

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

    public boolean ownsReview(Integer companyId, Long reviewId) {
        if (companyId == null || reviewId == null) return false;
        try {
            return reviewService.getOne(reviewId)
                    .map(ReviewDTO::getAccommodationId)
                    .map(accId -> ownsAccommodation(companyId, accId))
                    .orElse(false);
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    public boolean ownsInquiry(Integer companyId, Long inquiryId) {
        if (companyId == null || inquiryId == null) return false;
        try {
            InquiryDTO inquiry = inquiryService.get(inquiryId);
            return inquiry != null && ownsAccommodation(companyId, inquiry.getAccommodationId());
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    /* ===== @PreAuthorize SpEL 용: 로그인 사용자(authentication)의 회사 기준 ===== */

    public boolean ownsAccommodation(Long accommodationId, Authentication authentication) {
        return ownsAccommodation(currentCompanyId(authentication), accommodationId);
    }

    public boolean ownsRoom(Long roomId, Authentication authentication) {
        return ownsRoom(currentCompanyId(authentication), roomId);
    }

    public boolean ownsReservation(Long reservationId, Authentication authentication) {
        return ownsReservation(currentCompanyId(authentication), reservationId);
    }

    public boolean ownsReview(Long reviewId, Authentication authentication) {
        return ownsReview(currentCompanyId(authentication), reviewId);
    }

    public boolean ownsInquiry(Long inquiryId, Authentication authentication) {
        return ownsInquiry(currentCompanyId(authentication), inquiryId);
    }

    /** 선택(optional) 파라미터용: 값이 없으면 통과, 있으면 소유 숙소여야 한다. */
    public boolean ownsAccommodationIfPresent(Long accommodationId, Authentication authentication) {
        return accommodationId == null || ownsAccommodation(accommodationId, authentication);
    }

    public boolean canCreateReservation(Integer companyId, ReservationDTO form) {
        if (form == null || !ownsRoom(companyId, form.getRoomId())) return false;
        RoomDTO room = roomService.getByRoomId(form.getRoomId());
        Long roomAccommodationId = room.getAccommodationId();
        return form.getAccommodationId() == null || form.getAccommodationId().equals(roomAccommodationId);
    }
}
