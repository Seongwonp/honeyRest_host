package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.dtoOwner.DaySalesDTO;
import com.honeyrest.honeyrest_host.dtoOwner.MonthSalesDTO;
import com.honeyrest.honeyrest_host.dtoOwner.ReservationDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OSalesService {
    private final OReservationService reservationService;

    public List<DaySalesDTO> getDaySales(LocalDate startDate, LocalDate endDate) {
        List<ReservationDTO> reservations = reservationService.getReservations()
                .stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()) || "CONFIRMED".equals(r.getStatus()))
                .filter(r -> {
                    LocalDate date = r.getCheckOutDate();
                    return date.isAfter(startDate) && date.isBefore(endDate);
                })
                .sorted(Comparator.comparing(ReservationDTO::getCheckOutDate)) // 날짜순 정렬
                .toList();

        List<DaySalesDTO> dailySalesList = new ArrayList<>();

        for (ReservationDTO r : reservations) {
            LocalDate date = r.getCheckOutDate();

            // 이미 dailySalesList에 해당 날짜가 있는지 찾기
            DaySalesDTO existing = null;
            for (DaySalesDTO ds : dailySalesList) {
                if (ds.getDate().equals(date)) {
                    existing = ds;
                    break;
                }
            }

            if (existing == null) {
                // 없으면 새로 추가
                dailySalesList.add(new DaySalesDTO(date, r.getPrice(), 1));
            } else {
                // 있으면 값 누적
                int newCount = existing.getQuantity() + 1;
                BigDecimal newPrice = existing.getDayPrice().add(r.getPrice());

                // 기존 객체 값 변경 (setter 필요)
                existing.setQuantity(newCount);
                existing.setDayPrice(newPrice);
            }
        }

        return dailySalesList;
    }

    public List<MonthSalesDTO> getMonthSales(LocalDate startDate, LocalDate endDate) {
        List<ReservationDTO> reservations = reservationService.getReservations()
                .stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .filter(r -> {
                    LocalDate date = r.getCheckOutDate();
                    return !date.isBefore(startDate) && date.isBefore(endDate);
                })
                .sorted(Comparator.comparing(ReservationDTO::getCheckOutDate)) // 날짜순 정렬
                .toList();

        List<MonthSalesDTO> monthlySalesList = new ArrayList<>();

        for (ReservationDTO r : reservations) {
            YearMonth yearMonth = YearMonth.from(r.getCheckOutDate());

            // 이미 monthlySalesList에 해당 월이 있는지 찾기
            MonthSalesDTO existing = null;
            for (MonthSalesDTO ms : monthlySalesList) {
                if (ms.getDate().equals(yearMonth)) {
                    existing = ms;
                    break;
                }
            }

            if (existing == null) {
                // 없으면 새로 추가
                monthlySalesList.add(new MonthSalesDTO(yearMonth, r.getPrice(), 1));
            } else {
                // 있으면 값 누적
                int newCount = existing.getQuantity() + 1;
                BigDecimal newPrice = existing.getMonthPrice().add(r.getPrice());

                existing.setQuantity(newCount);
                existing.setMonthPrice(newPrice);
            }
        }
        return monthlySalesList;
    }

    public List<DaySalesDTO> getCompanyDaySales(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<ReservationDTO> reservations = reservationService.getReservationsByCompanyId(companyId)
                .stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .filter(r -> {
                    LocalDate date = r.getCheckOutDate();
                    return !date.isBefore(startDate) && date.isBefore(endDate);
                })
                .sorted(Comparator.comparing(ReservationDTO::getCheckOutDate)) // 날짜순 정렬
                .toList();

        List<DaySalesDTO> dailySalesList = new ArrayList<>();

        for (ReservationDTO r : reservations) {
            LocalDate date = r.getCheckOutDate();

            // 이미 dailySalesList에 해당 날짜가 있는지 찾기
            DaySalesDTO existing = null;
            for (DaySalesDTO ds : dailySalesList) {
                if (ds.getDate().equals(date)) {
                    existing = ds;
                    break;
                }
            }

            if (existing == null) {
                // 없으면 새로 추가
                dailySalesList.add(new DaySalesDTO(date, r.getPrice(), 1));
            } else {
                // 있으면 값 누적
                int newCount = existing.getQuantity() + 1;
                BigDecimal newPrice = existing.getDayPrice().add(r.getPrice());

                // 기존 객체 값 변경 (setter 필요)
                existing.setQuantity(newCount);
                existing.setDayPrice(newPrice);
            }
        }

        return dailySalesList;
    }

    public List<MonthSalesDTO> getCompanyMonthSales(Long companyId,LocalDate startDate, LocalDate endDate) {
        List<ReservationDTO> reservations = reservationService.getReservationsByCompanyId(companyId)
                .stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .filter(r -> {
                    LocalDate date = r.getCheckOutDate();
                    return !date.isBefore(startDate) && date.isBefore(endDate);
                })
                .sorted(Comparator.comparing(ReservationDTO::getCheckOutDate)) // 날짜순 정렬
                .toList();

        List<MonthSalesDTO> monthlySalesList = new ArrayList<>();

        for (ReservationDTO r : reservations) {
            YearMonth yearMonth = YearMonth.from(r.getCheckOutDate());

            // 이미 monthlySalesList에 해당 월이 있는지 찾기
            MonthSalesDTO existing = null;
            for (MonthSalesDTO ms : monthlySalesList) {
                if (ms.getDate().equals(yearMonth)) {
                    existing = ms;
                    break;
                }
            }

            if (existing == null) {
                // 없으면 새로 추가
                monthlySalesList.add(new MonthSalesDTO(yearMonth, r.getPrice(), 1));
            } else {
                // 있으면 값 누적
                int newCount = existing.getQuantity() + 1;
                BigDecimal newPrice = existing.getMonthPrice().add(r.getPrice());

                existing.setQuantity(newCount);
                existing.setMonthPrice(newPrice);
            }
        }
        return monthlySalesList;
    }

    public List<DaySalesDTO> getAccommodationDaySales(Long accommodationId, LocalDate startDate, LocalDate endDate) {
        List<ReservationDTO> reservations = reservationService.getReservationsByAccommodationId(accommodationId)
                .stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .filter(r -> {
                    LocalDate date = r.getCheckOutDate();
                    return !date.isBefore(startDate) && date.isBefore(endDate);
                })
                .sorted(Comparator.comparing(ReservationDTO::getCheckOutDate)) // 날짜순 정렬
                .toList();

        List<DaySalesDTO> dailySalesList = new ArrayList<>();

        for (ReservationDTO r : reservations) {
            LocalDate date = r.getCheckOutDate();

            // 이미 dailySalesList에 해당 날짜가 있는지 찾기
            DaySalesDTO existing = null;
            for (DaySalesDTO ds : dailySalesList) {
                if (ds.getDate().equals(date)) {
                    existing = ds;
                    break;
                }
            }

            if (existing == null) {
                // 없으면 새로 추가
                dailySalesList.add(new DaySalesDTO(date, r.getPrice(), 1));
            } else {
                // 있으면 값 누적
                int newCount = existing.getQuantity() + 1;
                BigDecimal newPrice = existing.getDayPrice().add(r.getPrice());

                // 기존 객체 값 변경 (setter 필요)
                existing.setQuantity(newCount);
                existing.setDayPrice(newPrice);
            }
        }

        return dailySalesList;
    }

    public List<MonthSalesDTO> getAccommodationMonthSales(Long accommodationId,LocalDate startDate, LocalDate endDate) {
        List<ReservationDTO> reservations = reservationService.getReservationsByAccommodationId(accommodationId)
                .stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .filter(r -> {
                    LocalDate date = r.getCheckOutDate();
                    return !date.isBefore(startDate) && date.isBefore(endDate);
                })
                .sorted(Comparator.comparing(ReservationDTO::getCheckOutDate)) // 날짜순 정렬
                .toList();

        List<MonthSalesDTO> monthlySalesList = new ArrayList<>();

        for (ReservationDTO r : reservations) {
            YearMonth yearMonth = YearMonth.from(r.getCheckOutDate());

            // 이미 monthlySalesList에 해당 월이 있는지 찾기
            MonthSalesDTO existing = null;
            for (MonthSalesDTO ms : monthlySalesList) {
                if (ms.getDate().equals(yearMonth)) {
                    existing = ms;
                    break;
                }
            }

            if (existing == null) {
                // 없으면 새로 추가
                monthlySalesList.add(new MonthSalesDTO(yearMonth, r.getPrice(), 1));
            } else {
                // 있으면 값 누적
                int newCount = existing.getQuantity() + 1;
                BigDecimal newPrice = existing.getMonthPrice().add(r.getPrice());

                existing.setQuantity(newCount);
                existing.setMonthPrice(newPrice);
            }
        }
        return monthlySalesList;
    }
}
