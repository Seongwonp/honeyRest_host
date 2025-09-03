package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,Long> {
    boolean existsByBusinessNumber(String businessNumber);
    Optional<Company> findByBusinessNumber(String businessNumber);

    // User <-> Company FK가 없으므로 이메일로 매칭
    @Query("""
        select c.companyId
          from Company c, User u
         where u.email = :email
           and u.email = c.email
    """)
    Optional<Long> findCompanyIdByUserEmail(@Param("email") String email);

    Company findCompanyByEmail(String email);

    Optional<Company> findByEmail(String email);
}

