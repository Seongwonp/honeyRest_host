package com.honeyrest.honeyrest_host.dtoOwner;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegionDTO {
    private Long regionId; // 지역 고유 Id
    private Long parentId; // 상위 지역
    private String name; // 지역명
    private Integer level; // 지역 레벨
    private boolean isPopular; // 인기 지역 여부
    private String imageUrl; // 지역 대표 이미지

}
