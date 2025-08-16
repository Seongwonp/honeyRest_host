package com.honeyrest.honeyrest_host.dto.accommodation;

import com.honeyrest.honeyrest_host.entity.enums.ImageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationImageDTO {

    private Long imageId;

    @NotBlank @Size(max = 500)
    private String imageUrl;

    @Size(max = 50)
    private ImageType imageType;

    @PositiveOrZero
    private Integer sortOrder;
}

