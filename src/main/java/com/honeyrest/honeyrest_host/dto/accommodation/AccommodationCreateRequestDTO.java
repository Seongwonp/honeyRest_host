package com.honeyrest.honeyrest_host.dto.accommodation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationCreateRequestDTO {

    // 연관키 (필수)
    @NotNull
    private Long companyId;
    @NotNull
    private Long categoryId;
    @NotNull
    private Long mainRegionId;
    @NotNull
    private Long subRegionId;

    private Long accommodationId;

    // 기본정보 (필수)
    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 500)
    private String address;

//    private Room totalRooms; -> 이미지 업로드를 위해 바인딩이 불가해서 주석처리함.
    // 위치
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private BigDecimal longitude;

    // 썸네일/설명
    @Size(max = 500)
    private String thumbnailUrl;

    private String description;

    // 편의시설(JSON) — 예: ["wifi","parking"]
    private String amenities;

    @Schema(type = "string", example = "15:00", description = "open (HH:mm)")
    @DateTimeFormat(pattern = "HH:mm")   // ★ 폼 바인딩용
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime checkInTime;

    @Schema(type = "string", example = "11:00", description = "close (HH:mm)")
    @DateTimeFormat(pattern = "HH:mm")   // ★ 폼 바인딩용
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime checkOutTime;

    // 상태 (미지정 시 서비스에서 "ACTIVE" 기본처리 권장)
    private String status;

    // 초기 부가정보 (선택)
    @Digits(integer = 8, fraction = 0)
    private BigDecimal minPrice;
    private BigDecimal rating;

    // 이미지 일괄 등록
    private List<AccommodationImageDTO> images;

    // 태그 매핑 (tag_id 목록)
    private List<Long> tagIds;
    private MultipartFile file;

    // 부가
    private List<AccommodationTagDTO> tags;

//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /* 상세 페이지에서 사용할 리스트용 getter */



}