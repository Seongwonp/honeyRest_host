package com.honeyrest.honeyrest_host.dtoOwner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationTagDTO {
    private Long tagId;
    private String name; // 태그명(오션뷰,바베큐 등)
    private String category; // 태그 카테고리
}
