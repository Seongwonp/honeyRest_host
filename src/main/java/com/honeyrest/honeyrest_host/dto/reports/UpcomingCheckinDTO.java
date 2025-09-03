package com.honeyrest.honeyrest_host.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpcomingCheckinDTO {
    private Long reservationId;
    private String guestName;
    private String accommodationName;
    private String roomName;
    private LocalDate checkIn;
    private Integer nights;
}
