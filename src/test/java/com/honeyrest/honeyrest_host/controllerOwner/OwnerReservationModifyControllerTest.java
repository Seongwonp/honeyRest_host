package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
import com.honeyrest.honeyrest_host.serviceCommon.ReservationConflictException;
import com.honeyrest.honeyrest_host.serviceOwner.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * POST /owner/reservation/modify 핸들러 단위 테스트 (MockMvc 없이 컨트롤러를 직접 호출).
 */
@ExtendWith(MockitoExtension.class)
class OwnerReservationModifyControllerTest {

    @Mock private OCompanyService companyService;
    @Mock private OAccommodationService accommodationService;
    @Mock private ORoomService roomService;
    @Mock private OReservationService reservationService;
    @Mock private OUserService userService;

    private ReservationController controller;

    @BeforeEach
    void setUp() {
        controller = new ReservationController(companyService, accommodationService, roomService,
                reservationService, userService);
    }

    private ReservationDTO form() {
        return ReservationDTO.builder().reservationId(7L).roomId(1L).status("CONFIRMED").build();
    }

    @Test
    void 수정_성공시_목록으로_리다이렉트하고_성공메시지를_남긴다() {
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        ReservationDTO dto = form();

        String view = controller.modifyReservation(dto, ra);

        verify(reservationService).modifyReservation(dto);
        assertThat(view).isEqualTo("redirect:/owner/reservation/list");
        assertThat(ra.getFlashAttributes()).containsKey("success");
    }

    @Test
    void 재고_부족이면_수정화면으로_돌아가_사유를_보여준다() {
        doThrow(new ReservationConflictException("남은 객실이 없습니다."))
                .when(reservationService).modifyReservation(any());
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.modifyReservation(form(), ra);

        assertThat(view).isEqualTo("redirect:/owner/reservation/7/modify");
        assertThat(ra.getFlashAttributes().get("error")).isEqualTo("남은 객실이 없습니다.");
    }

    @Test
    void 잘못된_상태값이면_수정화면으로_돌아간다() {
        doThrow(new IllegalArgumentException("유효하지 않은 예약 상태: X"))
                .when(reservationService).modifyReservation(any());
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.modifyReservation(form(), ra);

        assertThat(view).isEqualTo("redirect:/owner/reservation/7/modify");
        assertThat(ra.getFlashAttributes()).containsKey("error");
    }

    @Test
    void 없는_예약이면_목록으로_돌아간다() {
        doThrow(new EntityNotFoundException("해당 예약이 존재하지 않습니다."))
                .when(reservationService).modifyReservation(any());
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.modifyReservation(form(), ra);

        assertThat(view).isEqualTo("redirect:/owner/reservation/list");
        assertThat(ra.getFlashAttributes()).containsKey("error");
    }

    @Test
    void 예약ID가_없으면_서비스를_호출하지_않는다() {
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.modifyReservation(new ReservationDTO(), ra);

        verify(reservationService, never()).modifyReservation(any());
        assertThat(view).isEqualTo("redirect:/owner/reservation/list");
    }
}
