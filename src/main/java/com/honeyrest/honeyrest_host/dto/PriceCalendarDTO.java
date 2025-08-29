package com.honeyrest.honeyrest_host.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalendarDTO {
    //        전체 “월 캘린더”를 한 번에 전달할 때 사용. (조회 API 요청/응답에 적합)
    private Long companyId;          // 관리자 소속 회사
    private Long accommodationId;    // 특정 숙소 필터 (nullable)
    private String accommodationName;
    private YearMonth yearMonth;
    private List<RoomCalendarDTO> rooms;
}
