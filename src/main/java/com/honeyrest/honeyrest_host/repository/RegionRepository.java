package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByLevel(Integer level);
    List<Region> findByParentId(Long parentId);
}
