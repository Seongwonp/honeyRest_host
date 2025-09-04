package com.honeyrest.honeyrest_host.repositoryAdmin.accommodation;

import com.honeyrest.honeyrest_host.entity.AccommodationTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccommodationTagMapRepository extends JpaRepository<AccommodationTagMap, Long> {

    // 조회
    List<AccommodationTagMap> findByAccommodation_AccommodationId(Long accommodationId);

    // 삭제 (파생쿼리명은 서비스에서 그대로 호출)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByAccommodation_AccommodationId(Long accommodationId);
}