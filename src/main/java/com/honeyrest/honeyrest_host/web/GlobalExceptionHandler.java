package com.honeyrest.honeyrest_host.web;

import com.honeyrest.honeyrest_host.serviceCommon.ReservationConflictException;
import com.honeyrest.honeyrest_host.serviceAdmin.ErrorLogService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class GlobalExceptionHandler {

    private final ErrorLogService errorLogService;

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbidden(AccessDeniedException e, Model model) {
        model.addAttribute("message", "접근 권한이 없습니다.");
        return "error/403";
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception e, Model model) {
        model.addAttribute("message", "요청하신 페이지를 찾을 수 없습니다.");
        return "error/404";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFound(EntityNotFoundException e, Model model) {
        log.warn("EntityNotFound: {}", e.getMessage());
        model.addAttribute("message", "데이터를 찾을 수 없습니다.");
        return "error/404";
    }

    /** 재고 부족으로 예약 생성/점유 전환이 거절된 경우. 컨트롤러가 잡지 못했을 때의 기본 화면. */
    @ExceptionHandler(ReservationConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleReservationConflict(ReservationConflictException e, Model model) {
        log.info("ReservationConflict: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        model.addAttribute("errorCode", 409);
        model.addAttribute("errorTitle", "요청을 처리할 수 없습니다");
        return "error/500";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException e, Model model, HttpServletRequest request) {
        log.warn("BadRequest: {}", e.getMessage());
        errorLogService.saveAsync(e, request.getRequestURI(), request.getMethod());
        model.addAttribute("message", "잘못된 요청입니다.");
        model.addAttribute("errorCode", 400);
        model.addAttribute("errorTitle", "잘못된 요청입니다");
        return "error/500";
    }

    /**
     * 필수 파라미터 누락·형식 오류(예: /admin/reservations/abc)는 클라이언트 오류다.
     * 과거에는 아래 handleGeneral 로 떨어져 500 + ERROR 로그 + 에러 로그 테이블 적재가 됐다.
     */
    @ExceptionHandler({org.springframework.web.bind.MissingServletRequestParameterException.class,
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
            org.springframework.web.bind.MissingPathVariableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadParameter(Exception e, Model model, HttpServletRequest request) {
        log.warn("BadParameter [{}] {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        model.addAttribute("message", "요청 값이 올바르지 않습니다.");
        model.addAttribute("errorCode", 400);
        model.addAttribute("errorTitle", "잘못된 요청입니다");
        return "error/500";
    }

    /** POST 전용 주소를 주소창으로 열었을 때 등 */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String handleMethodNotAllowed(Exception e, Model model) {
        model.addAttribute("message", "이 주소는 화면에서 버튼으로만 실행할 수 있습니다.");
        model.addAttribute("errorCode", 405);
        model.addAttribute("errorTitle", "허용되지 않은 요청입니다");
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneral(Exception e, Model model, HttpServletRequest request) {
        log.error("Unhandled exception [{}] {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        errorLogService.saveAsync(e, request.getRequestURI(), request.getMethod());
        model.addAttribute("message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        return "error/500";
    }
}
