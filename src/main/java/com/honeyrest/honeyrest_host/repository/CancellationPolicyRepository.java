package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.CancellationPolicy;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CancellationPolicyRepository extends JpaRepository<CancellationPolicy, Long> {
    Optional<CancellationPolicy> findByAccommodation_AccommodationId(Long accommodationId);

    @Transactional
    void deleteByAccommodation_AccommodationId(Long accommodationId);

    @Modifying
    @Transactional
    @Query("update CancellationPolicy p set p.detail = :detail " +
           "where p.accommodation.accommodationId = :accId")
    int updateDetailByAccId(@Param("accId") Long accommodationId,
                            @Param("detail") String detail);
}
