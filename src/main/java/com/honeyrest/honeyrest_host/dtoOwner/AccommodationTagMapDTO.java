package com.honeyrest.honeyrest_host.dtoOwner;

import com.honeyrest.domain.entity.Accommodation;
import com.honeyrest.domain.entity.AccommodationTag;
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
