package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReviewDTO;

public interface ReviewService {


    // 리뷰 목록 전체 조회
//    PageResponseDTO<ReviewDTO> getList(String status, Long roomId, String sort, PageRequestDTO pageRequestDTO);

    // 리뷰 조회
    ReviewDTO getOne(Long reviewId);

    // 리뷰 등록
    ReviewDTO insert(ReviewDTO reviewDTO);

    // 리뷰 수정
    ReviewDTO update(Long reviewId, ReviewDTO reviewDTO);

    // 리뷰 삭제
    void delete(Long reviewId);

}
