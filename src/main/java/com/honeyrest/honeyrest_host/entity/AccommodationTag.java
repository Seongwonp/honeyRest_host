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
@Table(name = "accommodation_tag")
public class AccommodationTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(nullable = false, length = 100)
    private String name; // 태그명(오션뷰,바베큐 등)

    @Column(nullable = false, length = 50)
    private String category; // 태그 카테고리

    // 공유 스키마(사용자 API Flyway V1)의 컬럼명은 icon_name VARCHAR(50) 이다.
    // 이전 매핑(@Column 기본값 → icon)은 존재하지 않는 컬럼이라 ddl-auto=validate 에서 기동이 실패했다.
    @Column(name = "icon_name", length = 50)
    private String icon; // 태그 아이콘 (리액트 아이콘 이름 저장)


}
