package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OEventRepository extends JpaRepository<Event, Long> {
}
