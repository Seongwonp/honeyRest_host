package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.domain.type.ReservationStatus;
import com.honeyrest.honeyrest_host.dtoOwner.DaySalesDTO;
import com.honeyrest.honeyrest_host.dtoOwner.MonthSalesDTO;
import com.honeyrest.honeyrest_host.repositoryOwner.OSalesRepository;
import com.honeyrest.honeyrest_host.type.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * 총관리자 매출(일별/월별 × 전체/업체/숙소).
 * <p>
 * 기준: 결제일(payment_date) 기준, 결제 승인 완료(DONE) + 예약 미취소. 기간은 <b>시작일과 종료일을 모두 포함</b>한다
 * (화면 기본값이 "이번 달 1일 ~ 말일", "올해 1/1 ~ 12/31" 이다).
 * 과거 구현은 예약 체크아웃일·예약 금액 기준의 메모리 합산이었고 일별 전체 매출만 시작일을 빠뜨렸다(isAfter).
 * 이제 관리자 매출(SalesStatRepository)과 같은 결제 기준으로 통일했다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OSalesService {

    private final OSalesRepository salesRepository;

    public List<DaySalesDTO> getDaySales(LocalDate startDate, LocalDate endDate) {
        return daily(startDate, endDate, null, null);
    }

    public List<MonthSalesDTO> getMonthSales(LocalDate startDate, LocalDate endDate) {
        return monthly(startDate, endDate, null, null);
    }

    public List<DaySalesDTO> getCompanyDaySales(Integer companyId, LocalDate startDate, LocalDate endDate) {
        return daily(startDate, endDate, companyId, null);
    }

    public List<MonthSalesDTO> getCompanyMonthSales(Integer companyId, LocalDate startDate, LocalDate endDate) {
        return monthly(startDate, endDate, companyId, null);
    }

    public List<DaySalesDTO> getAccommodationDaySales(Long accommodationId, LocalDate startDate, LocalDate endDate) {
        return daily(startDate, endDate, null, accommodationId);
    }

    public List<MonthSalesDTO> getAccommodationMonthSales(Long accommodationId, LocalDate startDate, LocalDate endDate) {
        return monthly(startDate, endDate, null, accommodationId);
    }

    private List<DaySalesDTO> daily(LocalDate start, LocalDate end, Integer companyId, Long accommodationId) {
        return salesRepository.sumDaily(PaymentStatus.DONE, ReservationStatus.CANCELLED,
                        start.atStartOfDay(), end.plusDays(1).atStartOfDay(), companyId, accommodationId)
                .stream()
                .map(r -> new DaySalesDTO(r.getSalesDay(), r.getTotal(), r.getQuantity().intValue()))
                .toList();
    }

    private List<MonthSalesDTO> monthly(LocalDate start, LocalDate end, Integer companyId, Long accommodationId) {
        return salesRepository.sumMonthly(PaymentStatus.DONE, ReservationStatus.CANCELLED,
                        start.atStartOfDay(), end.plusDays(1).atStartOfDay(), companyId, accommodationId)
                .stream()
                .map(r -> new MonthSalesDTO(YearMonth.of(r.getSalesYear(), r.getSalesMonth()), r.getTotal(),
                        r.getQuantity().intValue()))
                .toList();
    }
}
