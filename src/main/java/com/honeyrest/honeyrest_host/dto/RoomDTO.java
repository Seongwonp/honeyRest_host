package com.honeyrest.honeyrest_host.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Long roomId;

    @NotNull(message = "업체 ID(accommodationId)는 필수입니다.")
    private Long accommodationId;

    // @NotBlank(message = "객실 이름은 필수입니다.")  // ← 선택
    private String roomName; // 객실 명
    private String accommodationName; // 숙소명

    private String type; // Standard, Deluxe...
    private BigDecimal price;            // 객실 기본 요금
    private Integer maxOccupancy;        // 최대 수용 인원
    private Integer standardOccupancy; // 기존 인원
    private BigDecimal extraPersonFee; // 추가 인원 요금
    private String bedInfo; // JSON String
    private String amenities; // JSON String
    private String description; // 설명
    private Integer totalRooms; // 총객실수
    private String status; // ACTIVE / INACTIVE

    private MultipartFile file; // 메인 이미지
    private List<MultipartFile> files; // 이미지 여러개

    private List<RoomImageDTO> images; // 이미지 타입들


    @DateTimeFormat(pattern = "HH:mm")   // ★ 화면 바인딩용
    private LocalDateTime displayCheckInTime;
    @DateTimeFormat(pattern = "HH:mm")   // ★ 화면 바인딩용
    private LocalDateTime displayCheckOutTime;




}