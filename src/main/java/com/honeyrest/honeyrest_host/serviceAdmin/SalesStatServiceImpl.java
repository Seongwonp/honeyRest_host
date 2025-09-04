package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dto.reports.*;
import com.honeyrest.honeyrest_host.repositoryAdmin.PaymentRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection.SalesStatRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesStatServiceImpl implements SalesStatService {


    private final PaymentRepository paymentRepo;
    private final ReservationRepository reservationRepo;

    private List<SalesStatDTO> convert(List<SalesStatRow> rows){
        return rows.stream().map(r -> SalesStatDTO.builder()
                .bucket(r.getBucket())
                .totalSales(r.getTotalSales())
                .totalOrders(r.getTotalOrders())
                .avgOrderPrice(r.getAvgOrderPrice())
                .dayOfWeek(r.getDayOfWeek())
                .build()).collect(Collectors.toList());
    }

    // ========== Payment 기준 ==========
    @Override
    public List<SalesStatDTO> getDailyPaymentSales(LocalDate from, LocalDate to){
        return convert(paymentRepo.findDailySales(from, to));
    }
    @Override
    public List<SalesStatDTO> getWeeklyPaymentSales(LocalDate from, LocalDate to){
        return convert(paymentRepo.findWeeklySales(from, to));
    }
    @Override
    public List<SalesStatDTO> getMonthlyPaymentSales(LocalDate from, LocalDate to){
        return convert(paymentRepo.findMonthlySales(from, to));
    }
    @Override
    public List<SalesStatDTO> getWeekdayPaymentSales(LocalDate from, LocalDate to){
        return convert(paymentRepo.findWeekdaySales(from, to));
    }

    // ========== Reservation 기준 ==========
    @Override
    public List<SalesStatDTO> getDailyReservationSales(LocalDate from, LocalDate to){
        return convert(reservationRepo.findDailyReservationSales(from, to));
    }
    @Override
    public List<SalesStatDTO> getWeeklyReservationSales(LocalDate from, LocalDate to){
        return convert(reservationRepo.findWeeklyReservationSales(from, to));
    }
    @Override
    public List<SalesStatDTO> getMonthlyReservationSales(LocalDate from, LocalDate to){
        return convert(reservationRepo.findMonthlyReservationSales(from, to));
    }
    @Override
    public List<SalesStatDTO> getWeekdayReservationSales(LocalDate from, LocalDate to){
        return convert(reservationRepo.findWeekdayReservationSales(from, to));
    }
}
