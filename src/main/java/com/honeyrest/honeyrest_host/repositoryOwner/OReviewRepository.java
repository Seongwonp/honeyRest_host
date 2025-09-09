package com.honeyrest.honeyrest_host.repositoryOwner;

import com.honeyrest.honeyrest_host.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByAccommodationId(Long accommodationId, Pageable pageable);

    List<Review> findByRoomId(Long roomId);

    Page<Review> findByRoomId(Long roomId, Pageable pageable);

    Review findByReviewId(Long reviewId);

    Page<Review> findByUser_UserId(Long  userId, Pageable pageable);
}
