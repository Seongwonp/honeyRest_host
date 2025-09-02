package com.honeyrest.honeyrest_host.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserListDTO {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private int point;               // 잔액
    private Boolean UsedPoints;       // 사용 여부
    private LocalDateTime lastPointAt;   // 최근 포인트 이벤트
    private Integer reservationCount;
    private LocalDate lastReservationDate;
    private Integer reviewCount;
    private Double avgRating;
    private LocalDateTime lastLogin;
    private String status;


}
