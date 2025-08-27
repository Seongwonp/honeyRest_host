package com.honeyrest.honeyrest_host.service;


import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;

import java.util.List;

public interface ReservationService {
    // 예약 현황으로 목록 조회
    PageResponseDTO<ReservationDTO> getReservationsByStatus(String status, PageRequestDTO pageRequestDTO);

    // 예약 번호로 조회
    ReservationDTO getReservationByNumber(String number);

    // 예약 id로 조회
    ReservationDTO getReservationById(Long reservationId);

    // 예약 변경
    Reservation updateReservation(ReservationDTO reservationDTO);

    // 예약 취소
    void canceledReservation(Long reservationId, String cancelReason);

    // 예약 등록
    ReservationDTO createReservation(ReservationDTO reservationDTO);

    PageResponseDTO<ReservationDTO> getAllReservations(PageRequestDTO pageRequestDTO);

    List<ReservationDTO> getAllReservationsNoPaging();

    long countAll();

    PageResponseDTO<ReservationDTO> getCompanyReservations(Long companyId, String status, String q, PageRequestDTO pageRequestDTO);


}
