package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.SalesChartPointDTO;

import java.time.LocalDate;
import java.util.List;

public interface SalesChartService {
    // 핵심 메서드 (스위치 한 곳에 몰아넣기)
    List<SalesChartPointDTO> getChart(String mode,
                                      List<Long> accIds,
                                      LocalDate from,
                                      LocalDate to);
}
