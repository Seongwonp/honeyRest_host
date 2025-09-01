package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends  JpaRepository<User, Long> {
    User findByEmail(String email);


    @Query("select u.name from User u where u.userId = :id")
    Optional<String> findNameById(@Param("id") Long id);
}
