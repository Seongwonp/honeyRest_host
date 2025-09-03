package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
