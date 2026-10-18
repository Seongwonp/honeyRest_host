package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.SalesChartPointDTO;
import com.honeyrest.honeyrest_host.repositoryAdmin.SalesStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 매출 차트. 집계 단위별 조회·행 변환은 {@link ChartMode} 가 담당한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesChartServiceImpl implements SalesChartService {
    private final SalesStatRepository repo;

    @Override
    public List<SalesChartPointDTO> getChart(String mode, List<Long> accIds, LocalDate from, LocalDate to) {
        ChartMode chartMode = ChartMode.parse(mode);
        return chartMode.fetch(repo, accIds, from, to).stream()
                .map(chartMode::toPoint)
                .toList();
    }
}
