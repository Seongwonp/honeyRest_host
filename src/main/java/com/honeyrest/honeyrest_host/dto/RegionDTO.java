package com.honeyrest.honeyrest_host.dto;

import com.honeyrest.honeyrest_host.entity.Region;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegionDTO {
    private Long regionId; // 고유 지역 id
    private Long parentId; //상위 지역
    private String name;
    private Integer level;

    public static RegionDTO of(Region r) {
        return new RegionDTO(r.getRegionId(), r.getParentId(), r.getName(), r.getLevel());
    }

}
