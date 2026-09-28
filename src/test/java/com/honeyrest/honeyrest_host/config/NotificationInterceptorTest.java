package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 알림 배지 값(_notifyCancelCount)이 리다이렉트 URL 의 쿼리스트링으로 새지 않아야 한다.
 */
class NotificationInterceptorTest {

    private final ReservationRepository reservationRepository = mock(ReservationRepository.class);
    private final AccommodationRepository accommodationRepository = mock(AccommodationRepository.class);
    private final NotificationInterceptor interceptor =
            new NotificationInterceptor(reservationRepository, accommodationRepository);

    private void login() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "contact@honeyrest.com", null, List.of(new SimpleGrantedAuthority("ROLE_COMPANY_ADMIN"))));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 리다이렉트_응답에는_알림_값을_싣지_않는다() {
        login();
        ModelAndView mav = new ModelAndView("redirect:/admin/price/page?companyId=1&ym=2026-09");

        interceptor.postHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), new Object(), mav);

        assertThat(mav.getModel()).doesNotContainKey("_notifyCancelCount");
        verifyNoInteractions(accommodationRepository, reservationRepository);
    }

    @Test
    void 일반_화면에는_알림_값을_싣는다() {
        login();
        when(accommodationRepository.findAccommodationIdsByAdminEmail("contact@honeyrest.com")).thenReturn(List.of(1L));
        when(reservationRepository.countCancelRequestByAccommodationIds(anyList())).thenReturn(2L);
        ModelAndView mav = new ModelAndView("admin/price/page");

        interceptor.postHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), new Object(), mav);

        assertThat(mav.getModel()).containsEntry("_notifyCancelCount", 2L);
    }
}
