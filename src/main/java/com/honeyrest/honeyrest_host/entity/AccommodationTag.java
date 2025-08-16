package com.honeyrest.honeyrest_host.entity;

import com.honeyrest.honeyrest_host.entity.enums.TagCategory;
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
@Table(name = "accommodation_tag")
public class AccommodationTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(nullable = false, length = 100)
    private String name; // 태그명(오션뷰,바베큐 등)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TagCategory category; // 태그 카테고리
}
