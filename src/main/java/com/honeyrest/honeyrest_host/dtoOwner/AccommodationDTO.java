package com.honeyrest.honeyrest_host.dtoOwner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

// AccommodationDto.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationDTO {
    private Long AccommodationId;
    private Long companyId;
    private Long categoryId;
    private Long mainRegionId;
    private Long subRegionId;
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private MultipartFile file;
    private String thumbnailUrl; // 저장된 이미지 URL
    private MultipartFile[] images; // 여러 장의 숙소 이미지
    private String amenities; // JSON String
    private String description;
    private LocalTime checkInTime;
    private LocalTime  checkOutTime;
    private BigDecimal rating; // 평균 평점
    private BigDecimal minPrice; // 최저 가격
    private String status; // ACTIVE / INACTIVE
}
