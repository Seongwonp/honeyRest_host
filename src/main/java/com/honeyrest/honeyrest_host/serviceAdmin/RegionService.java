package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dto.RegionDTO;



import java.util.List;

public interface RegionService {
    // 대지역 전체
    List<RegionDTO> listMainRegions();                 // level=1

    // 부모지역의 하위(소지역) 전체
    List<RegionDTO> listSubRegions(Long parentId);     // 하위 지역

    RegionDTO get(Long regionId);               // ★ 단건 조회 추가
}
