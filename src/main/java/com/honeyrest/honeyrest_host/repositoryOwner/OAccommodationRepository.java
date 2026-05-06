package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.honeyrest_host.entity.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OAccommodationRepository extends JpaRepository<Accommodation, Long> {

    @EntityGraph(attributePaths = {"company", "category", "mainRegion", "subRegion"})
    List<Accommodation> findByCompany_CompanyId(Long companyCompanyId);

    @EntityGraph(attributePaths = {"company", "category", "mainRegion", "subRegion"})
    Page<Accommodation> findByCompany_CompanyId(Long companyId, Pageable pageable);

    Accommodation findByAccommodationId(Long accommodationId);

    @EntityGraph(attributePaths = {"company", "category", "mainRegion", "subRegion"})
    List<Accommodation> findByCompany_CompanyIdAndNameContainingIgnoreCase(Long companyId, String name);

    @EntityGraph(attributePaths = {"company", "category", "mainRegion", "subRegion"})
    List<Accommodation> findByNameContainingIgnoreCase(String keyword);

    Accommodation findByName(String accommodationName);
}
