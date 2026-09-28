package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    long count();
    @Query("select count(r) from Room r where r.accommodation.company.companyId = :companyId")
    long countByCompanyId(@Param("companyId") Integer companyId);

    // 페이징
    Page<Room> findByAccommodation_AccommodationId(Long accommodationId, Pageable pageable);

    void deleteByAccommodation_AccommodationId(Long accommodationId);

    /**
     * 예약 생성/점유 상태 전환 시 객실 행을 잠근다 (SELECT ... FOR UPDATE).
     * 사용자 저장소 RoomRepository.findByIdForUpdate 와 같은 용도.
     * 재고는 total_rooms 를 차감하지 않고 겹치는 점유 예약 수로 계산한다
     * (과거 decreaseStock/increaseStock 는 total_rooms 자체를 바꿔 객실 수를 오염시켜 제거했다).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.roomId = :roomId")
    Optional<Room> findByIdForUpdate(@Param("roomId") Long roomId);

    // companyId는 accommodation → company 로 타고 감 (엔티티 매핑 기준)
    @Query("""
                select r from Room r
                join r.accommodation a
                join a.company c
                where c.companyId = :companyId
                  and (:accommodationId is null or a.accommodationId = :accommodationId)
            """)
    Page<Room> findRoomsOfCompany(@Param("companyId") Integer companyId,
                                  @Param("accommodationId") Long accommodationId, Pageable pageable);


    List<Room> findAllByAccommodation_Company_CompanyId(Integer companyId);


    // 숙소까지 한번에 로딩 (상세 페이지용)
    @EntityGraph(attributePaths = {"accommodation"})
    @Query("select r from Room r where r.roomId = :id")
    Optional<Room> findByIdWithAccommodation(Long id);

    // fetch join으로 즉시 로딩 (멀티라인 X, 별칭 제거)
    @Query("select r from Room r join fetch r.accommodation where r.roomId = :roomId")
    Optional<Room> findWithAccommodationByRoomId(@Param("roomId") Long roomId);




    // 목록에서 숙소명 필요할 때 (N+1 방지)
    @EntityGraph(attributePaths = {"accommodation"})
    @Query("select r from Room r")
    List<Room> findAllWithAccommodation();


    // 비활성화 토글 형식으로 전환
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Room r set r.status = CASE WHEN r.status = 'ACTIVE' THEN 'INACTIVE' ELSE 'ACTIVE' END where r.roomId = :roomId")
    int toggleStatus(@Param("roomId") Long roomId);


    // 객실상세페이지, 숙소 체크인,체크아웃 값 표시하기
    @Query("""
            select r from Room r
            join fetch r.accommodation a
            where r.roomId = :roomId
            """)
    // PK가 roomId인 경우
    @EntityGraph(attributePaths = "accommodation")
    Optional<Room> findByRoomId(Long roomId);

    // 또는, PK가 roomId이고 JpaRepository의 findById를 재정의해도 됨
    @EntityGraph(attributePaths = "accommodation")
    Optional<Room> findById(Long roomId);

    @Query("select r.name from Room r where r.roomId = :id")
    Optional<String> findNameById(@Param("id") Long id);
}

