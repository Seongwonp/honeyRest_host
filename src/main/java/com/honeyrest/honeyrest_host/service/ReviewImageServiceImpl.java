package com.honeyrest.honeyrest_host.service;


import com.honeyrest.honeyrest_host.dto.ReviewImageDTO;
import com.honeyrest.honeyrest_host.entity.Review;
import com.honeyrest.honeyrest_host.entity.ReviewImage;
import com.honeyrest.honeyrest_host.repository.ReviewImageRepository;
import com.honeyrest.honeyrest_host.repository.ReviewRepository;
import com.honeyrest.honeyrest_host.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewImageServiceImpl implements ReviewImageService {

    private final ReviewImageRepository reviewImageRepository;
    private final ReviewRepository reviewRepository;
    private final FileUploadUtil fileUploadUtil;

    private static final String FOLDER = "reviews";          // Firebase 상의 폴더명


    private ReviewImageDTO toDTO(ReviewImage reviewImage) {
        return ReviewImageDTO.builder()
                .imageId(reviewImage.getImageId())
                .reviewId(reviewImage.getReview().getReviewId())
                .imageUrl(reviewImage.getImageUrl())
                .build();
    }

    // 특정 리뷰 모든 이미지 dto 조회
    @Override
    public List<ReviewImageDTO> getImages(Long reviewId) {
        return reviewImageRepository.findAllByReview_ReviewIdOrderByImageIdAsc(reviewId)
                .stream().map(this::toDTO).toList();
    }

    // 여러 리뷰의 모든 이미지
    @Override
    public Map<Long, List<ReviewImageDTO>> getImagesForReviews(Collection<Long> reviewIds) {
        return reviewImageRepository.findAllByReview_ReviewIdIn(reviewIds)
                .stream().map(this::toDTO)
                .collect(Collectors.groupingBy(ReviewImageDTO::getReviewId));
    }

    // 특정 리뷰의 이미지 개수
    @Override
    public int getImageCount(Long reviewId) {
        return reviewImageRepository.countByReview_ReviewId(reviewId);
    }


    @Transactional
    @Override
    public List<ReviewImageDTO> uploadImages(Long reviewId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return List.of();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 없음 " + reviewId));

        List<ReviewImageDTO> result = new java.util.ArrayList<>();
        for (MultipartFile f : files) {
            if (f == null || f.isEmpty()) continue;

            try {
                String url = fileUploadUtil.upload(f, FOLDER); // ★ Firebase 업로드 → 공개 URL
                ReviewImage saved = reviewImageRepository.save(
                        ReviewImage.builder()
                                .review(review)
                                .imageUrl(url)
                                .build()
                );
                result.add(toDTO(saved));
            } catch (Exception e) {
                throw new RuntimeException("이미지 업로드 실패: " + f.getOriginalFilename(), e);
            }
        }
        return result;

    }

    /* 리뷰별 이미지 목록 조회 (DTO) */
    @Transactional(readOnly = true)
    @Override
    public List<ReviewImageDTO> findByReviewId(Long reviewId) {
        return reviewImageRepository.findAllByReview_ReviewIdOrderByImageIdAsc(reviewId).stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * 개별 이미지 삭제 (파일까지 함께 삭제할지 선택)
     */
    @Transactional
    @Override
    public void deleteImage(Long reviewId, Long imageId, boolean deleteFileAlso) {
        ReviewImage img = reviewImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("이미지 없음: " + imageId));
        try {
            fileUploadUtil.delete(FOLDER, img.getImageUrl()); // ★ Firebase에서 파일 삭제
        } catch (Exception ignore) {
            // 파일이 이미 없을 수도 있으니, DB 삭제는 계속 진행
        }
        reviewImageRepository.delete(img);
    }

}
