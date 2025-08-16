package com.honeyrest.honeyrest_host.dto.accommodation;

import com.honeyrest.honeyrest_host.entity.enums.TagCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationTagMapDTO {
    private Long tagId;
    private String name;
    private TagCategory category;
}