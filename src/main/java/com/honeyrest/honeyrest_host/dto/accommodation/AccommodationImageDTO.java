package com.honeyrest.honeyrest_host.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationImageDTO {

    private Long imageId; // 필수

    private Long accommodationId;

    @Size(max = 500)
    private String imageUrl; // 저장 이미지 경로

    @Size(max = 50)
    private String imageType; // 기본: 메인

    @PositiveOrZero
    private Integer sortOrder; // 옵션

    private MultipartFile file; // 업로드 요청 할때에만 사용

}

