package com.honeyrest.honeyrest_host.dtoAdmin;

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
public class RoomCalendarDTO {
    //    특정 숙소/객실의 월간 캘린더 단위 표현.
    private RoomHeaderDTO room;
    private YearMonth yearMonth;
    private List<CalendarCellDTO> days;
}
