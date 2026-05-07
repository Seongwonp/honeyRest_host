package com.honeyrest.honeyrest_host.web;

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
        model.addAttribute("message", "데이터를 찾을 수 없습니다: " + e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException e, Model model, HttpServletRequest request) {
        log.warn("BadRequest: {}", e.getMessage());
        errorLogService.saveAsync(e, request.getRequestURI(), request.getMethod());
        model.addAttribute("message", "잘못된 요청입니다: " + e.getMessage());
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
