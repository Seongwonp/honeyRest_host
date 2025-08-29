package com.honeyrest.honeyrest_host.repository;

import com.honeyrest.honeyrest_host.entity.Review;
import com.honeyrest.honeyrest_host.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByAccommodationId(Long accommodationId, Pageable pageable);
}
