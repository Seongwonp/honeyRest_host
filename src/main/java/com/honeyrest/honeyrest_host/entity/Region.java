package com.honeyrest.honeyrest_host.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "region")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Integer regionId; // 지역 고유 Id

    @Column(name = "parent_id")
    private Integer parentId; // 상위 지역

    @Column(name = "name", nullable = false, length = 100)
    private String name; // 지역명

    @Column(name = "level",nullable = false)
    private Integer level; // 지역 레벨

    @Column(name = "is_popular")
    private boolean isPopular; // 인기 지역 여부

    @Column(name = "image_url", length=255)
    private String imageUrl; // 지역 대표 이미지

}
