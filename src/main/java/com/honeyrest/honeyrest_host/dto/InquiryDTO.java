package com.honeyrest.honeyrest_host.dto;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InquiryDTO {

    private Long inquiryId;

    private Long userId;
    private String userName; // 추가: 작성자 이름 (화면 표시용)

    private Long accommodationId;
    private String accommodationName; // 추가: 숙소명 (화면 표시용)

    private String title; // 제목

    private String content; // 문의 내용

    private String reply; // 답변 내용

    private Boolean isReplied;  // 답변 여부


    private String category;  // 문의 카테고리

    private LocalDateTime createdAt; // 문의 작성일
    private LocalDateTime updatedAt; // 마지막 수정일


}
