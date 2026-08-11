package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.honeyrest_host.entity.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OAccommodationRepository extends JpaRepository<Accommodation, Long> {

    /** 원자적 상태 전이: 현재 상태가 expectedStatus일 때만 newStatus로 바뀐다. 영향받은 행 수를 반환한다. */
    @Modifying
    @Query("UPDATE Accommodation a SET a.status = :newStatus WHERE a.accommodationId = :id AND a.status = :expectedStatus")
    int updateStatusIfCurrent(@Param("id") Long id, @Param("expectedStatus") String expectedStatus, @Param("newStatus") String newStatus);

    @EntityGraph(attributePaths = {"company", "category", "mainRegion", "subRegion"})
    List<Accommodation> findByCompany_CompanyId(Integer companyCompanyId);

    @EntityGraph(attributePaths = {"company", "category", "mainRegion", "subRegion"})
    Page<Accommodation> findByCompany_CompanyId(Integer companyId, Pageable pageable);

    Accommodation findByAccommodationId(Long accommodationId);

    @EntityGraph(attributePaths = {"company", "category", "mainRegion", "subRegion"})
    List<Accommodation> findByCompany_CompanyIdAndNameContainingIgnoreCase(Integer companyId, String name);

    @EntityGraph(attributePaths = {"company", "category", "mainRegion", "subRegion"})
    List<Accommodation> findByNameContainingIgnoreCase(String keyword);

    Accommodation findByName(String accommodationName);
}
