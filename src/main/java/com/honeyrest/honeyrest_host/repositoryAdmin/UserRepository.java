package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.UserListDTO;
import com.honeyrest.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends  JpaRepository<User, Long> {
    User findByEmail(String email);

    /** 포인트 잔액 변경 전 회원 행을 잠근다 (사용자 API 의 포인트 사용과 동시에 일어나도 잔액이 어긋나지 않게). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.userId = :id")
    Optional<User> findByIdForUpdate(@Param("id") Long id);

    @Query("select u.name from User u where u.userId = :id")
    Optional<String> findNameById(@Param("id") Long id);

    @Query("""
    select new com.honeyrest.honeyrest_host.dtoAdmin.UserListDTO(
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
