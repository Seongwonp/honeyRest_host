package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.honeyrest_host.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface OReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findReservationsByAccommodation_AccommodationId(Long accommodationId);

    List<Reservation> findReservationsByAccommodation_Company_CompanyId(Integer CompanyId);

    @Query("SELECT r FROM Reservation r WHERE r.room.roomId = :roomId " +
            "AND (r.checkInDate <= :endDate AND r.checkOutDate >= :startDate)")
    List<Reservation> findByRoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);

    Page<Reservation> findByAccommodation_AccommodationId(Long accommodationId, Pageable pageable);

    Page<Reservation> findByAccommodation_Company_CompanyId(Integer CompanyId, Pageable pageable);

    Page<Reservation> findByRoom_RoomId(Long roomId, Pageable pageable);

    // 방별, 기간별 예약 조회
    @Query("SELECT r FROM Reservation r WHERE r.room.roomId = :roomId " +
            "AND r.checkInDate <= :endDate AND r.checkOutDate >= :startDate " +
            "AND r.status != 'CANCELLED'")
    List<Reservation> findByRoomIdAndDateRange(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // ---- 오너 예약 목록: 상태 필터를 DB 쿼리에서 적용해 목록과 total(count)이 같은 조건을 쓰도록 한다 ----
    // companyId / accommodationId 가 null 이면 전체 대상. 상태 비교는 대소문자를 무시한다(UPPER).

    /** 업체 기준, 지정 상태 제외 (예: 활성 목록 = CANCELLED, CANCEL_REQUEST 제외) */
    @Query(value = """
                select r from Reservation r
                 where (:companyId is null or r.accommodation.company.companyId = :companyId)
                   and upper(r.status) not in :statuses
                """,
            countQuery = """
                select count(r) from Reservation r
                 where (:companyId is null or r.accommodation.company.companyId = :companyId)
                   and upper(r.status) not in :statuses
                """)
    Page<Reservation> findPageByCompanyExcludingStatuses(@Param("companyId") Integer companyId,
                                                         @Param("statuses") Collection<String> statuses,
                                                         Pageable pageable);

    /** 업체 기준, 지정 상태만 (예: 취소 요청 목록) */
    @Query(value = """
                select r from Reservation r
                 where (:companyId is null or r.accommodation.company.companyId = :companyId)
                   and upper(r.status) in :statuses
                """,
            countQuery = """
                select count(r) from Reservation r
                 where (:companyId is null or r.accommodation.company.companyId = :companyId)
                   and upper(r.status) in :statuses
                """)
    Page<Reservation> findPageByCompanyWithStatuses(@Param("companyId") Integer companyId,
                                                    @Param("statuses") Collection<String> statuses,
                                                    Pageable pageable);

    /** 숙소 기준, 지정 상태 제외 */
    @Query(value = """
                select r from Reservation r
                 where (:accommodationId is null or r.accommodation.accommodationId = :accommodationId)
                   and upper(r.status) not in :statuses
                """,
            countQuery = """
                select count(r) from Reservation r
                 where (:accommodationId is null or r.accommodation.accommodationId = :accommodationId)
                   and upper(r.status) not in :statuses
                """)
    Page<Reservation> findPageByAccommodationExcludingStatuses(@Param("accommodationId") Long accommodationId,
                                                               @Param("statuses") Collection<String> statuses,
                                                               Pageable pageable);

    /** 숙소 기준, 지정 상태만 */
    @Query(value = """
                select r from Reservation r
                 where (:accommodationId is null or r.accommodation.accommodationId = :accommodationId)
                   and upper(r.status) in :statuses
                """,
            countQuery = """
                select count(r) from Reservation r
                 where (:accommodationId is null or r.accommodation.accommodationId = :accommodationId)
                   and upper(r.status) in :statuses
                """)
    Page<Reservation> findPageByAccommodationWithStatuses(@Param("accommodationId") Long accommodationId,
                                                          @Param("statuses") Collection<String> statuses,
                                                          Pageable pageable);
}
