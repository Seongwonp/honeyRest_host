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
@Table(name = "Accommodation_Tag")
public class AccommodationTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private int tagId;

    @Column(nullable = false, length = 100)
    private String name; // 태그명(오션뷰,바베큐 등)

    @Column(nullable = false, length = 50)
    private String category; // 태그 카테고리
}
