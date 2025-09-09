package com.honeyrest.honeyrest_host.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardDTO {
    long accCount;         // 숙소 수
    long resCount;         // 예약 건수(취소 제외 등 정책 반영)
    long roomCount;        // 객실 수
}
