package com.honeyrest.honeyrest_host.dtoOwner;

import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AccommodationCategoryDTO {
    private Long categoryId; // 카테고리 고유 식별자
    private String name; // 카테고리명
    private String iconUrl; // 카테고리 아이콘 이미지 경로
    private Integer sortOrder; // 정렬 순서
}
