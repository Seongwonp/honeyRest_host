package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OCompanyRepository extends JpaRepository<Company,Long> {
    List<Company> findByName(String keyword);
}
