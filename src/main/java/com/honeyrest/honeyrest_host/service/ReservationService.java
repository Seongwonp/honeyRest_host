package com.honeyrest.honeyrest_host.service;


import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;

public interface ReservationService {
    // 예약 현황으로 목록 조회
    PageResponseDTO<ReservationDTO> getReservationsByStatus(String status, PageRequestDTO pageRequestDTO);

    // 예약 번호로 조회
    ReservationDTO getReservationByNumber(String number);

    // 예약 id로 조회
    Reservation getReservationById(Long reservationId);

    // 예약 변경
    Reservation updateReservation(ReservationDTO reservationDTO);

    // 예약 취소
    void canceledReservation(Long reservationId, String cancelReason);
}
