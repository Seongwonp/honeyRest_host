package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.ReservationDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2

class ReservationServiceImplTest {
    @Autowired
    private ReservationService reservationService;

    @Test
    void getReservationsByStatus() {

    }
    private Long roomId;

    @Test
    void getReservationByNumber() {
    }

    @Test
    void getReservationById() {
    }

    @Test
    void updateReservation() {

    }

    @Test
    void canceledReservation() {
    }

    @Test
    void createReservation() {
        ReservationDTO reservationDTO = ReservationDTO.builder()
                .accommodationId(3L)
                .accommodationName("test 숙소")
                .userId(3L)
                .roomId(3L)
                .guestName("김짱구")
                .guestPhone("010-1234-5678")
                .price(BigDecimal.valueOf(100000))
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(2))
                .status("예약 완료")
                .build();

        log.info("TEST dto.roomId = " + reservationDTO.getRoomId()); // ← 이 값이 1 아니면 DTO 빌더 문제
        assertNotNull(reservationDTO.getRoomId()); // Long 이어야 의미 있음saved);

    }
}