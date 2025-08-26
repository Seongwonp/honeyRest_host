package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
