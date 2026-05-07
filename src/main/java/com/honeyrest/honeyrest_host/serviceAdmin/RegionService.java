package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.RegionDTO;



import java.util.List;

public interface RegionService {
    // 대지역 전체
    List<RegionDTO> listMainRegions();                 // level=1

    // 부모지역의 하위(소지역) 전체
    List<RegionDTO> listSubRegions(Integer parentId);     // 하위 지역

    RegionDTO get(Integer regionId);               // ★ 단건 조회 추가
}
