package com.honeyrest.honeyrest_host.repositoryAdmin.accommodation;

import com.honeyrest.honeyrest_host.entity.AccommodationTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccommodationTagMapRepository extends JpaRepository<AccommodationTagMap, Long> {
    // 조회
    List<AccommodationTagMap> findByAccommodation_AccommodationId(Long accId);

    // 삭제 (대량 삭제 시 성능/영속성 컨텍스트 동기화)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    void deleteByAccommodation_AccommodationId(Long accId);
}
