package com.honeyrest.honeyrest_host.dto.accommodation;

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
    private String category;
}