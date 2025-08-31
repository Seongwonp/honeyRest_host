package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    List<Accommodation> findByCompany_CompanyId(Long companyCompanyId);

    Page<Accommodation> findByCompany_CompanyId(Long companyId, Pageable pageable);

    Accommodation findByAccommodationId(Long accommodationId);

    List<Accommodation> findByCompany_CompanyIdAndNameContainingIgnoreCase(Long companyId, String name);

    List<Accommodation> findByNameContainingIgnoreCase(String keyword);

}
