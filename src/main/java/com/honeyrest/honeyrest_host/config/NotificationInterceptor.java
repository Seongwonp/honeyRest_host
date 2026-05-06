package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {

    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView mav) {

        if (mav == null || !mav.hasView()) return;
        // Ajax/API 요청 제외
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) return;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) return;

        try {
            String email = auth.getName();
            // 이 계정이 관리하는 숙소 ID 목록 조회
            var accIds = accommodationRepository.findAccommodationIdsByAdminEmail(email);
            long cancelCount = 0;

            if (accIds != null && !accIds.isEmpty()) {
                // 취소요청 대기 건수 조회
                cancelCount = reservationRepository.countCancelRequestByAccommodationIds(accIds);
            }

            mav.addObject("_notifyCancelCount", cancelCount);
        } catch (Exception ignored) {
            // 알림 조회 실패해도 페이지 동작은 정상 유지
        }
    }
}
