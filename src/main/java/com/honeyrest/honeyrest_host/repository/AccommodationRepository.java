package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    List<Accommodation> findByCompany_CompanyId(Long companyCompanyId);
}
