package com.honeyrest.honeyrest_host.dto;

import com.honeyrest.honeyrest_host.entity.Accommodation;
import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationImageDTO {
    private Long imageId; // 숙소 이미지 고유 식별자
    private Accommodation accommodation; // 숙소 ID
    private String imageUrl; // 이미지 경로
    private String imageType; // 이미지 종류
    private Integer sortOrder; //정렬 순서

}
