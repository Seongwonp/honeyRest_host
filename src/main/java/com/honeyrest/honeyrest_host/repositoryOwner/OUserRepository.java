package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OUserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Page<User> findAll(Pageable pageable);

    User findByNameAndPhone(String name, String phone);

    /** 회원 목록 검색 (이름·이메일 부분 일치) */
    @org.springframework.data.jpa.repository.Query(
            "select u from User u where lower(u.name) like lower(concat('%', :kw, '%'))"
            + " or lower(u.email) like lower(concat('%', :kw, '%'))")
    Page<User> searchByKeyword(@org.springframework.data.repository.query.Param("kw") String keyword, Pageable pageable);

    List<User> findByNameContainingIgnoreCase(String name);

    /** 예약 등록 화면의 회원 선택용 (일반 회원, 활성 계정) */
    List<User> findByRoleAndStatusOrderByNameAsc(String role, String status);
}
