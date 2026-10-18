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
 * 총관리자(오너) 매출 화면용 집계. 관리자 SalesStatRepository 와 같은 기준이다:
 * 결제 승인 완료(DONE) + 예약이 취소되지 않음, 결제일 기준, [from 00:00, toExclusive 00:00).
 * <p>
 * 과거에는 OSalesService 가 모든 예약을 메모리로 읽어 여섯 군데에서 복사된 반복문으로 합산했고,
 * 그중 하나는 isAfter 를 써서 시작일 매출이 빠졌다. 이제 DB 의 GROUP BY 한 번으로 집계한다.
 * 범위(전체/업체/숙소)는 null 이면 조건을 끄는 파라미터로 한 쿼리에서 처리한다.
 * JPQL 이라 MySQL(운영)과 H2(test/screenshot)에서 같은 쿼리가 돈다.
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
