package com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection;

import java.util.Date;

public interface UpcomingCheckinRow {
    Long getReservationId();
    String getGuestName();
    String getAccommodationName();
    String getRoomName();
    Date getCheckIn();
    Integer getNights();
}
