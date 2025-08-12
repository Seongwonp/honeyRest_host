package com.honeyrest.honeyrest_host.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accommodation_image")
public class AccommodationImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId; // 숙소 이미지 고유 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation; // 숙소 ID

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl; // 이미지 경로

    @Column(name = "image_type", length = 50)
    private String imageType; // 이미지 종류

    @Column(name = "sort_order")
    private Integer sortOrder; //정렬 순서


}
