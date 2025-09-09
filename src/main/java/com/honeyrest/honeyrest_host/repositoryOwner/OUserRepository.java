package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.honeyrest_host.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OUserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Page<User> findAll(Pageable pageable);

    User findByNameAndPhone(String name, String phone);

    List<User> findByNameContainingIgnoreCase(String name);
}
