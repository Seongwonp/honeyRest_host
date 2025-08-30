package com.honeyrest.honeyrest_host.service;


import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    // 예약 현황으로 목록 조회
    PageResponseDTO<ReservationDTO> getCompanyReservations(Long companyId, String status, String q, PageRequestDTO pageRequestDTO);

    // 예약 번호로 조회
    ReservationDTO getReservationByNumber(String number);

    // 예약 id로 조회
    ReservationDTO getReservationById(Long reservationId);

    // 예약 변경
    ReservationDTO updateReservation(ReservationDTO reservationDTO);

    // 예약 취소
    void cancelReservation(Long reservationId, String cancelReason);

    // 예약 등록
    ReservationDTO createReservation(ReservationDTO reservationDTO);

    // 캘린더/겹침 조회
    /*특정 객실(roomId)의 기간과 겹치는 예약들 */
    List<ReservationDTO> findRoomReservationsOverlapping(Long roomId, LocalDate start, LocalDate end);

    /*회사(선택: 숙소)의 기간과 겹치는 예약들 */
    List<ReservationDTO> findCompanyReservationsOverlapping(Long companyId, Long accommodationId, LocalDate start, LocalDate end);

    /*회사(선택: 숙소)의 ‘하루’ 예약(= date 00:00~다음날 00:00 겹침) */
    List<ReservationDTO> findCompanyReservationsOnDate(Long companyId, Long accommodationId, LocalDate date);

    ReservationDTO getReservationDetail(Long id);
}

