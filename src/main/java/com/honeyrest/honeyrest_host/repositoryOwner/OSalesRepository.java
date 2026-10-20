package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 총관리자 매출 화면용 집계. SalesStatRepository 와 같은 기준(결제 DONE + 예약 미취소, 결제일 기준).
 * 범위(전체/업체/숙소)는 null 이면 조건을 끄는 파라미터로 처리한다.
 */
public interface OSalesRepository extends JpaRepository<Payment, Long> {

    interface DailyRow {
        LocalDate getSalesDay();
        BigDecimal getTotal();
        Long getQuantity();
    }

    interface MonthlyRow {
        Integer getSalesYear();
        Integer getSalesMonth();
        BigDecimal getTotal();
        Long getQuantity();
    }

    @Query("""
            select cast(p.paymentDate as LocalDate) as salesDay,
                   coalesce(sum(p.amount), 0)       as total,
                   count(distinct r.reservationId)  as quantity
              from Payment p
              join p.reservation r
              join r.accommodation a
             where p.paymentStatus = :paymentStatus
               and r.status <> :excludedStatus
               and p.paymentDate >= :from
               and p.paymentDate <  :toExclusive
               and (:companyId is null or a.company.companyId = :companyId)
               and (:accommodationId is null or a.accommodationId = :accommodationId)
             group by cast(p.paymentDate as LocalDate)
             order by cast(p.paymentDate as LocalDate)
            """)
    List<DailyRow> sumDaily(@Param("paymentStatus") String paymentStatus,
                            @Param("excludedStatus") String excludedStatus,
                            @Param("from") LocalDateTime from,
                            @Param("toExclusive") LocalDateTime toExclusive,
                            @Param("companyId") Integer companyId,
                            @Param("accommodationId") Long accommodationId);

    @Query("""
            select year(p.paymentDate)             as salesYear,
                   month(p.paymentDate)            as salesMonth,
                   coalesce(sum(p.amount), 0)      as total,
                   count(distinct r.reservationId) as quantity
              from Payment p
              join p.reservation r
              join r.accommodation a
             where p.paymentStatus = :paymentStatus
               and r.status <> :excludedStatus
               and p.paymentDate >= :from
               and p.paymentDate <  :toExclusive
               and (:companyId is null or a.company.companyId = :companyId)
               and (:accommodationId is null or a.accommodationId = :accommodationId)
             group by year(p.paymentDate), month(p.paymentDate)
             order by year(p.paymentDate), month(p.paymentDate)
            """)
    List<MonthlyRow> sumMonthly(@Param("paymentStatus") String paymentStatus,
                                @Param("excludedStatus") String excludedStatus,
                                @Param("from") LocalDateTime from,
                                @Param("toExclusive") LocalDateTime toExclusive,
                                @Param("companyId") Integer companyId,
                                @Param("accommodationId") Long accommodationId);
}
