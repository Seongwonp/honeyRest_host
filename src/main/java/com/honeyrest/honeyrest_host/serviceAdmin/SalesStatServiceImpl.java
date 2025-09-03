package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dto.reports.*;
import com.honeyrest.honeyrest_host.repositoryAdmin.PaymentRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.projection.DailySalesProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesStatServiceImpl implements SalesStatService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    /**
     * 업체(companyId) 기준 일별 매출 집계.
     * zeroFill=true 이면 from~to 사이의 모든 날짜를 0으로 채워 반환.
     */
    public List<SalesStatDTO> getDailySales(Long companyId, LocalDate from, LocalDate to, boolean zeroFill) {
        // 1) DB에서 일별 집계 조회(프로젝션)
        List<DailySalesProjection> rows = paymentRepository.findDailySalesByCompany(companyId, from, to);

        // 2) 프로젝션 -> DTO 변환 (그대로 반환할 리스트)
        List<SalesStatDTO> result = new ArrayList<>(rows.size());
        for (DailySalesProjection p : rows) {
            SalesStatDTO dto = SalesStatDTO.builder()
                    .bucket(p.getBucket().toLocalDate())
                    .totalSales(p.getTotalSales() != null ? p.getTotalSales() : BigDecimal.ZERO)
                    .totalOrders(p.getTotalOrders() != null ? p.getTotalOrders() : 0)
                    .avgOrderPrice(p.getAvgOrderPrice() != null ? p.getAvgOrderPrice() : BigDecimal.ZERO)
                    .build();
            result.add(dto);
        }

        if (!zeroFill) {
            // 정렬 보장 (DB에서 정렬했더라도 안전하게)
            result.sort(Comparator.comparing(SalesStatDTO::getBucket));
            return result;
        }

        // 3) zero-fill: 날짜 -> DTO 맵 (빠른 조회용)
        Map<LocalDate, SalesStatDTO> byDate = new HashMap<>(result.size() * 2);
        for (SalesStatDTO r : result) {
            byDate.put(r.getBucket(), r);
        }

        // 4) from ~ to 모든 날짜 순회하며 없으면 0으로 채움
        List<SalesStatDTO> filled = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            SalesStatDTO dto = byDate.get(d);
            if (dto == null) {
                dto = SalesStatDTO.builder()
                        .bucket(d)
                        .totalSales(BigDecimal.ZERO)
                        .totalOrders(0)
                        .avgOrderPrice(BigDecimal.ZERO)
                        .build();
            }
            filled.add(dto);
        }

        // 5) 날짜 오름차순 보장 후 반환
        // (for 루프가 이미 from→to 순서라 사실상 정렬 불필요하지만, 안전차원에서 남겨둠)
        filled.sort(Comparator.comparing(SalesStatDTO::getBucket));
        return filled;
    }

    @Override
    public List<TopAccommodationDTO> getTopAccommodations(Long companyId, LocalDate from, LocalDate to, int limit) {
        List<Object[]> rows = paymentRepository.findTopAccommodations(companyId, from, to, limit);
        List<TopAccommodationDTO> list = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            list.add(TopAccommodationDTO.builder()
                    .accommodationId(((Number) r[0]).longValue())
                    .accommodationName((String) r[1])
                    .totalSales((BigDecimal) r[2])
                    .build());
        }
        return list;
    }

    @Override
    public List<UpcomingCheckinDTO> getUpcomingCheckins(Long companyId, LocalDate date, int size) {
        List<Object[]> rows = reservationRepository.findUpcomingCheckins(companyId, date, size);
        List<UpcomingCheckinDTO> list = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            list.add(UpcomingCheckinDTO.builder()
                    .reservationId(((Number) r[0]).longValue())
                    .guestName((String) r[1])
                    .accommodationName((String) r[2])
                    .roomName((String) r[3])
                    .checkIn(((java.sql.Timestamp) r[4]).toLocalDateTime().toLocalDate())
                    .nights(((Number) r[5]).intValue())
                    .build());
        }
        return list;
    }

    @Override
    public CancelSummaryDTO getCancellationSummary(Long companyId, LocalDate from, LocalDate to) {
        Object[] row = reservationRepository.findCancelSummary(companyId, from, to);
        if (row == null) return CancelSummaryDTO.empty();

        int canceled = ((Number) row[0]).intValue();
        int total    = ((Number) row[1]).intValue();
        double rate  = (total > 0) ? (canceled * 1.0 / total) : 0.0;

        return CancelSummaryDTO.builder()
                .canceled(canceled)
                .total(total)
                .cancelRate(rate)
                .build();
    }

    @Override
    public OccupancyDTO getOccupancy(Long companyId, LocalDate from, LocalDate to) {
        // 총 객실 수
        int totalRooms = (int) roomRepository.countByCompanyId(companyId);
        int days = (int) (to.toEpochDay() - from.toEpochDay() + 1);
        int availableNights = Math.max(totalRooms * days, 0);

        // 판매박수
        int soldNights = Optional.ofNullable(
                reservationRepository.calcSoldNights(companyId, from, to)).orElse(0);

        // 승인 매출 합계
        BigDecimal sales = Optional.ofNullable(
                paymentRepository.sumSales(companyId, from, to)).orElse(BigDecimal.ZERO);

        // ADR / RevPAR / 점유율
        BigDecimal adr = (soldNights > 0)
                ? sales.divide(BigDecimal.valueOf(soldNights), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal revpar = (availableNights > 0)
                ? sales.divide(BigDecimal.valueOf(availableNights), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        double occupancy = (availableNights > 0)
                ? (soldNights * 1.0 / availableNights)
                : 0.0;

        return OccupancyDTO.builder()
                .occupancyRate(occupancy)
                .adr(adr)
                .revpar(revpar)
                .soldNights(soldNights)
                .availableNights(availableNights)
                .build();
    }
}
