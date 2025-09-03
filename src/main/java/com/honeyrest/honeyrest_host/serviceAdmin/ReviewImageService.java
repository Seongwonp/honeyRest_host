package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dto.ReviewImageDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ReviewImageService {
    // 특정 리뷰 모든 이미지
    List<ReviewImageDTO> getImages(Long reviewId);

    // 여러 리뷰의 모든 이미지
    Map<Long , List<ReviewImageDTO>> getImagesForReviews(Collection<Long> reviewIds);

    // 특정 리뷰의 이미지 개수
    int getImageCount(Long reviewId);

    @Transactional
    List<ReviewImageDTO>  uploadImages(Long reviewId, List<MultipartFile> files);

    /* 리뷰별 이미지 목록 조회 (DTO) */
    @Transactional(readOnly = true)
    List<ReviewImageDTO> findByReviewId(Long reviewId);

    @Transactional
    void deleteImage(Long reviewId,Long imageId, boolean deleteFileAlso);
}
