package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.domain.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OCompanyRepository extends JpaRepository<Company,Integer> {
    List<Company> findByName(String keyword);

    /** 활성 업체 페이지 (소문자 "active" 포함) */
    @Query(value = "select c from Company c where upper(c.status) = 'ACTIVE'"
            + " and (:kw is null or c.name like concat('%', :kw, '%') or c.ownerName like concat('%', :kw, '%'))",
            countQuery = "select count(c) from Company c where upper(c.status) = 'ACTIVE'"
            + " and (:kw is null or c.name like concat('%', :kw, '%') or c.ownerName like concat('%', :kw, '%'))")
    Page<Company> findActive(@Param("kw") String keyword, Pageable pageable);

    /** 비활성(활성이 아닌 모든 상태, 상태 없음 포함) 업체 페이지 */
    @Query(value = "select c from Company c where c.status is null or upper(c.status) <> 'ACTIVE'",
            countQuery = "select count(c) from Company c where c.status is null or upper(c.status) <> 'ACTIVE'")
    Page<Company> findInactive(Pageable pageable);
}
