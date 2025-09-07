package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.DashboardDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.DailySalesDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.MonthlySalesDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.TopRoomDTO;


import java.time.LocalDate;
import java.util.List;


public interface DashboardService {

    AdminLoginRequestDTO getCurrentAdmin(String adminEmail);


    List<TopRoomDTO> getTopRooms(List<Long> accommodationIds, LocalDate from, LocalDate to, int limit);

    // 최근 7일(일별), 여러 숙소 합산
    DashboardDTO getCountsFor(String adminEmail);

    List<DailySalesDTO> getRecentDailyForAccommodations(List<Long> accIds, int days);


    // 최근 12개월(월별) 여러 숙소 합산
    List<MonthlySalesDTO> getRecentMonthly12ForAccommodations(List<Long> accommodationIds);
}

