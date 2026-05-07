package com.honeyrest.honeyrest_host.dtoAdmin.accommodation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationUpdateRequestDTO {
    // 수정은 부분 업데이트 허용 가정 → 전부 nullable
    private Integer companyId;
    private Integer categoryId;
    private Integer mainRegionId;
    private Integer subRegionId;

    private String name;
    private String address;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private String thumbnail;
    private String description;

    private String amenities;         // ["wifi", ...]

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalDateTime checkInTime;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalDateTime checkOutTime;

    private String status;              // ACTIVE / INACTIVE
    private BigDecimal minPrice;

    // 선택: 이미지/태그 전체 교체(덮어쓰기)를 할지, 부분 수정할지는 정책에 따라
    private List<AccommodationImageDTO> images; // 덮어쓰기 정책(예시)
    private List<Long> tagIds;                          // 덮어쓰기 정책(예시)

    // 정책 textarea 바인딩용
    private String policyMultiline;

    // 환불 규정 추가
    private String cancellationPolicyDetail; // JSON 문자열 그대로 넣거나
    private List<String> cancellationPolicyItems; // 파싱된 리스트로 내려주기 용도


}



