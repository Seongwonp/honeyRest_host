package com.honeyrest.honeyrest_host.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalendarDTO {

    private Long roomId;

    //        전체 “월 캘린더”를 한 번에 전달할 때 사용. (조회 API 요청/응답에 적합)
    private Integer companyId;          // 관리자 소속 회사
    private Long accommodationId;    // 특정 숙소 필터 (nullable)
    private String accommodationName;
    private YearMonth yearMonth;
    private List<RoomCalendarDTO> rooms;


    // 하루 셀용( 추가)
    private BigDecimal price;
    private Integer availableRoom; // 해당 날짜에 예약 가능한 객실 수
    private LocalDate date;

    // ▶ 추가 권장: 쿼리 범위 메타데이터(overview/grid 양쪽 공용)
    private LocalDate startDate;
    private LocalDate endDate;

    // ▶ 추가 권장: 일자별 집계(overview) 결과
    private List<DailyOverviewDTO> dailyOverview;

    // ▶ 추가 권장: 방×일자 그리드 결과
    private List<GridCellDTO> grid;
}

