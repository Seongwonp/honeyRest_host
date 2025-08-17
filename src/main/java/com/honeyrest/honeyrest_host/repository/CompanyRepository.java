package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,Long> {
    boolean existsByBusinessNumber(String businessNumber);
    Optional<Company> findByBusinessNumber(String businessNumber);

}