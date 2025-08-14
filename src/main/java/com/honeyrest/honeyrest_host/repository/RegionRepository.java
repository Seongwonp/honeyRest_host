package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}