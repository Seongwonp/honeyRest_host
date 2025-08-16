package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenValue(String tokenValue);  // ← 필드명과 동일

    void deleteByUserUserId(Long userId); // 사용자 기준 삭제 등 필요시
}
