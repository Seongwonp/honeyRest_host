package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReviewDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ReviewService {


    // 인터페이스에 둘 중 하나만 노출 (getOne 추천)
    @Transactional(readOnly = true)
    Optional<ReviewDTO> getOne(Long reviewId);

    // 리뷰 목록 전체 조회
    PageResponseDTO<ReviewDTO> getList(String status, Long roomId, Long accommodationId,String sort, PageRequestDTO pageRequestDTO);

    // 리뷰 등록
    ReviewDTO insert(ReviewDTO reviewDTO);

    // 리뷰 수정
    ReviewDTO update(Long reviewId, ReviewDTO reviewDTO);

    // 리뷰 삭제
    void delete(Long reviewId);

    ReviewDTO patch(Long reviewId, ReviewDTO reviewDTO);

    @Transactional
    void changeStatus(Long reviewId, String status);

    @Transactional
    ReviewDTO toggleVisibleHidden(Long reviewId);
}
