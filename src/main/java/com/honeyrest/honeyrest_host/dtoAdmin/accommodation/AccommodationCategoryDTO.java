package com.honeyrest.honeyrest_host.dtoAdmin.accommodation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationCategoryDTO {
    private Long categoryId;
    private String name;
    private String iconUrl;
    private Integer sortOrder;
}