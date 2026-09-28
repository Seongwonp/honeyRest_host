package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.domain.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Integer> {
    List<Region> findByLevel(Integer level);
    List<Region> findByParentId(Integer parentId);
}
