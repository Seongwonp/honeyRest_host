package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company,Long> {
}