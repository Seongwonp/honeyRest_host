package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.ReviewDTO;

import com.honeyrest.honeyrest_host.entity.Review;
import com.honeyrest.honeyrest_host.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;


//    @Override
//    public PageResponseDTO<ReviewDTO> getList(String status, Long roomId, String sort, PageRequestDTO pageRequestDTO) {
//

//        // 최신/ 오래된 리뷰 조회 가져오기 (기본: 최신순)
//        Sort sortSpec = "old".equalsIgnoreCase(sort)
//                ? Sort.by("createdAt").ascending()
//                : Sort.by("createdAt").descending();
//
//        Pageable pageable = pageRequestDTO.getPageable(sortSpec);
//
//        Specification<Review> specification = Specification.where(null);
//
//        if (status != null && !status.isBlank()) {
//            specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status));
//
//        }
//        if (roomId != null && !status.isBlank()) {
//            specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("room"), roomId));
//        }
//        Page<Review> reviewPage = reviewRepository.findAll(specification, pageable);
//        List<ReviewDTO> dtoList = reviewPage.getContent().stream()
//                .map(review -> modelMapper.map(review, ReviewDTO.class))
//                .toList();
//
//        return PageResponseDTO.<ReviewDTO>withALl()
//                .pageRequestDTO(pageRequestDTO)
//                .dtoList(dtoList)
//                .total((int) reviewPage.getTotalElements())
//                .build();
//
//    }


    // 예약 리뷰 단건 조회
    @Override
    public ReviewDTO getOne(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException(" id=" + reviewId));
        return modelMapper.map(review, ReviewDTO.class);
    }

    // 리뷰 등록
    @Override
    public ReviewDTO insert(ReviewDTO reviewDTO) {
        if(reviewDTO.getReservationId() == null) {
            throw new IllegalArgumentException("reservationId is null");
        }
        if(reviewDTO.getUserId() == null) {
            throw new IllegalArgumentException("userId is null");
        }
        if(reviewDTO.getRating() == null) {
            throw new IllegalArgumentException("rating is null");
        }


//         Reservation reservation = reviewRepository.findById(reviewDTO.getReservationId()).orElseThrow(() ->new EntityNotFoundException("예약을 찾을 수 없습ㄴ다. Id=" + reviewDTO.getReviewId());
//        User user = userRepository (reviewDTO.getUserId())

        return null;
    }


    // 리뷰 수정
    @Override
    public ReviewDTO update(Long reviewId, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        ReviewDTO patch = ReviewDTO.builder()
                .rating(reviewDTO.getRating())
                .cleanlinessRating(reviewDTO.getCleanlinessRating())
                .serviceRating(reviewDTO.getServiceRating())
                .locationRating(reviewDTO.getLocationRating())
                .content(reviewDTO.getContent())
                .reply(reviewDTO.getReply())
                .status(reviewDTO.getStatus())
                .build();

        // dto -> 엔티티
        modelMapper.map(review, patch);

        return modelMapper.map(patch, ReviewDTO.class);
    }


    // 리뷰 삭제
    @Override
    public void delete(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId));
        reviewRepository.delete(review);

    }
}
