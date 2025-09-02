package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.dto.UserListDTO;
import com.honeyrest.honeyrest_host.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends  JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query("select u.name from User u where u.userId = :id")
    Optional<String> findNameById(@Param("id") Long id);

    @Query("""
    select new com.honeyrest.honeyrest_host.dto.UserListDTO(
        u.userId, u.name, u.email, u.phone,
        cast(u.point as integer),
        null, null, null, null, null, null,
        u.lastLogin,
        u.status
    )
    from User u
    where (:q is null
        or lower(u.name) like lower(concat('%', :q, '%'))
        or lower(u.email) like lower(concat('%', :q, '%'))
        or replace(u.phone,'-','') like replace(concat('%', :q, '%'),'-','')
    )
    """)
    Page<UserListDTO> findUserList(String q, Pageable pageable);
}
