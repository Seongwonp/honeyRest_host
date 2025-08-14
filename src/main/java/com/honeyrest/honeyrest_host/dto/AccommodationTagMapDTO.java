package com.honeyrest.honeyrest_host.dto;

import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.AccommodationTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationTagMapDTO {
    private Long mapId;
    private Accommodation accommodation;
    private AccommodationTag tag;

}
