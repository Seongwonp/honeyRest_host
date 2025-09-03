package com.honeyrest.honeyrest_host.service;


import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    List<ReservationDTO> findCompanyReservationsOnDate(Long companyId,
                                                       Long accommodationId,
                                                       LocalDate date);

    PageResponseDTO<ReservationDTO> getCancelRequestsForCompany(Long companyId, String q, PageRequestDTO pr);

    @Transactional
    ReservationDTO createReservation(ReservationDTO dto);

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


    // 캘린더/겹침 조회
    /*특정 객실(roomId)의 기간과 겹치는 예약들 */
    List<ReservationDTO> findRoomReservationsOverlapping(Long roomId, LocalDate start, LocalDate end);

    List<ReservationDTO> findCompanyReservationsOverlapping(Long companyId, Long accommodationId, LocalDate start, LocalDate end);

    ReservationDTO getReservationDetail(Long id);

    Page<ReservationDTO> getCompanyReservations(
            Long companyId, String status, String q, Long accId, Pageable pageable);

    // 예약 상태 전환 로그 , 알림
    ReservationDTO approveCancelRequest(Long reservationId, String reason); // 서비스에서 db 업데이트.dto 매핑해서 리던 -> json 으로 담기
    void rejectCancelRequest(Long reservationId, String reason);

    void markCompleted(Long reservationId);

    void markNoShow(Long reservationId);
}

