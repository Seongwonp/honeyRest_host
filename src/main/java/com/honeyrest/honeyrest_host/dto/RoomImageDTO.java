package com.honeyrest.honeyrest_host.dto;
import com.honeyrest.honeyrest_host.entity.Room;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomImageDTO {

    private Long roomId;       // FK
    private String imageUrl;   // 업로드 후 저장된 URL
    private Integer sortOrder; // 정렬 순서
    private String imageType;  // MAIN,SUB


}


