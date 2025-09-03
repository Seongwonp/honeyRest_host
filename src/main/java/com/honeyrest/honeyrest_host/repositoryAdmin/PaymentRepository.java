package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.Payment;
import com.honeyrest.honeyrest_host.repositoryAdmin.projection.DailySalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 최신 결제 1건
    Optional<Payment> findTopByReservationReservationIdOrderByCreatedAtDesc(Long reservationId);
    /*
     * 결제 내역 검색 jpql
     * - 회사 Id, 숙소 id 로 필터링
     * 결제 수단, 경제 상태, 기간, 검색어(예약번호/이름/전화) 지원
     * page 형태로 리턴(페이징 처리)
     */
    /* 결제 내역 화면(관리자 -> 결제내역, 정신 리포트) 에서 검색 조건 줬을 떄 사용됨 : 주어진 조건이 있으면 필터링, 없으면 전체 허용하는 JPQL */
    @Query("""
            select p from Payment p
            join p.reservation r where (:companyId is null or r.accommodation.company.companyId = :companyId) 
            and(:accommodationId is null or r.accommodation.accommodationId = :accommodationId) 
            and(:methodsEmpty = true or p.paymentMethod in :methods) 
            and(:statusesEmpty = true or p.paymentStatus in :statuses) 
            and(:from is null or p.paymentDate >= :from) 
            and(:to is null or p.paymentDate < :to) 
            and(
            :q is null or :q = '' or
            r.reservationNumber like concat('%', :q, '%') or 
            r.guestName like concat('%', :q, '%') or
            r.guestPhone like concat('%', :q, '%') 
            )
            """)
    Page<Payment> search(
            @Param("companyId") Long companyId,
            @Param("accommodationId") Long accommodationId,
            @Param("methods") List<String> methods,
            @Param("methodsEmpty") boolean methodsEmpty, // 리스트 비었을 때 true
            @Param("statuses") List<String> statuses,
            @Param("statusesEmpty") boolean statusesEmpty, // 리스트 비었을 때 true
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("q") String q,
            Pageable pageable
    );
    @Query(value = """
        SELECT 
            DATE(p.payment_date)         AS bucket,
            COALESCE(SUM(p.amount), 0)   AS totalSales,
            COUNT(*)                     AS totalOrders,
            COALESCE(AVG(p.amount), 0)   AS avgOrderPrice
        FROM payment p
        JOIN reservation r ON r.reservation_id = p.reservation_id
        JOIN accommodation a ON a.accommodation_id = r.accommodation_id
        WHERE a.company_id = :companyId
          AND p.payment_status = 'SUCCESS'            -- 문자열 비교 (Enum X)
          AND p.payment_date BETWEEN :from AND :to
        GROUP BY DATE(p.payment_date)
        ORDER BY DATE(p.payment_date)
        """, nativeQuery = true)
    List<DailySalesProjection> findDailySalesByCompany(
            @Param("companyId") Long companyId,
            @Param("from") java.time.LocalDate from,
            @Param("to") java.time.LocalDate to
    );

    // 숙소별 시리즈가 필요하면 accommodation_id/name까지 group by
    @Query(value = """
        SELECT 
            DATE(p.payment_date)         AS bucket,
            COALESCE(SUM(p.amount), 0)   AS totalSales,
            COUNT(*)                     AS totalOrders,
            COALESCE(AVG(p.amount), 0)   AS avgOrderPrice,
            a.accommodation_id           AS accommodationId,
            a.name                       AS accommodationName
        FROM payment p
        JOIN reservation r ON r.reservation_id = p.reservation_id
        JOIN accommodation a ON a.accommodation_id = r.accommodation_id
        WHERE a.company_id = :companyId
          AND p.payment_status = 'SUCCESS'
          AND p.payment_date BETWEEN :from AND :to
        GROUP BY DATE(p.payment_date), a.accommodation_id, a.name
        ORDER BY DATE(p.payment_date), a.accommodation_id
        """, nativeQuery = true)
    List<DailySalesProjection> findDailySalesByCompanyAndAccommodation(
            @Param("companyId") Long companyId,
            @Param("from") java.time.LocalDate from,
            @Param("to") java.time.LocalDate to
    );

    // 회사 기준 Top-N 숙소 (금액 합계 내림차순)
    @Query(value = """
        SELECT 
            a.accommodation_id   AS accommodationId,
            a.name               AS accommodationName,
            COALESCE(SUM(p.amount), 0) AS totalSales
        FROM payment p
        JOIN reservation r ON r.reservation_id = p.reservation_id
        JOIN accommodation a ON a.accommodation_id = r.accommodation_id
        WHERE a.company_id = :companyId
          AND p.payment_status = 'SUCCESS'
          AND p.payment_date BETWEEN :from AND :to
        GROUP BY a.accommodation_id, a.name
        ORDER BY totalSales DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopAccommodations(@Param("companyId") Long companyId,
                                         @Param("from") LocalDate from,
                                         @Param("to") LocalDate to,
                                         @Param("limit") int limit);

    // 기간 승인매출 합계
    @Query(value = """
        SELECT COALESCE(SUM(p.amount), 0)
        FROM payment p
        JOIN reservation r ON r.reservation_id = p.reservation_id
        JOIN accommodation a ON a.accommodation_id = r.accommodation_id
        WHERE a.company_id = :companyId
          AND p.payment_status = 'SUCCESS'
          AND p.payment_date BETWEEN :from AND :to
        """, nativeQuery = true)
    BigDecimal sumSales(@Param("companyId") Long companyId,
                        @Param("from") LocalDate from,
                        @Param("to") LocalDate to);

}
