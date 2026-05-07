package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.ErrorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {

    Page<ErrorLog> findByResolvedOrderByOccurredAtDesc(boolean resolved, Pageable pageable);

    Page<ErrorLog> findAllByOrderByOccurredAtDesc(Pageable pageable);

    long countByResolved(boolean resolved);

    @Modifying
    @Query("UPDATE ErrorLog e SET e.resolved = true WHERE e.errorLogId = :id")
    int markResolved(@Param("id") Long id);
}
