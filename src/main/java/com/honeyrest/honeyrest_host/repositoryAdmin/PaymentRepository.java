package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.Payment;
import com.honeyrest.honeyrest_host.repositoryAdmin.projection.DailySalesProjection;
import com.honeyrest.honeyrest_host.repositoryAdmin.reports.projection.SalesStatRow;
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
    /* 결제 내역 화면(관리자 -> 결제내역, 정산 리포트)에서 검색 조건 줬을 때 사용됨.
     * companyId는 테넌트 경계이므로 null-bypass를 허용하지 않는다(항상 필수 일치).
     * 그 외 조건은 주어지면 필터링, 없으면 전체 허용. */
    @Query("""
            select p from Payment p
            join p.reservation r where r.accommodation.company.companyId = :companyId
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
            @Param("companyId") Integer companyId,
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

    }