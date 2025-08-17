package com.honeyrest.honeyrest_host.dtoOwner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {
    private Long eventId;
    private String title;
    private String description;
    private String imageUrl;
    private String targetUrl;
    private Boolean isActive;

}
