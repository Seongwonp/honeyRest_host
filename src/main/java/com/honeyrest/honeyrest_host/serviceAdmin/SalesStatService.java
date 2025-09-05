package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.reports.*;

import java.time.LocalDate;
import java.util.List;

public interface SalesStatService {

    // ========== Payment 기준 ==========
    List<SalesStatDTO> getDailyPaymentSales(LocalDate from, LocalDate to);

    List<SalesStatDTO> getWeeklyPaymentSales(LocalDate from, LocalDate to);

    List<SalesStatDTO> getMonthlyPaymentSales(LocalDate from, LocalDate to);

    List<SalesStatDTO> getWeekdayPaymentSales(LocalDate from, LocalDate to);

    // ========== Reservation 기준 ==========
    List<SalesStatDTO> getDailyReservationSales(LocalDate from, LocalDate to);

    List<SalesStatDTO> getWeeklyReservationSales(LocalDate from, LocalDate to);

    List<SalesStatDTO> getMonthlyReservationSales(LocalDate from, LocalDate to);

    List<SalesStatDTO> getWeekdayReservationSales(LocalDate from, LocalDate to);
}
