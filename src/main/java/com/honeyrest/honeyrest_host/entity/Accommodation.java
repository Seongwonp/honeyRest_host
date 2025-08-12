package com.honeyrest.honeyrest_host.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Time;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accommodation")
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_id", nullable = false)
    private Long accommodationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private AccommodationCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_region_id", nullable = false)
    private Region mainRegion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_region_id", nullable = false)
    private Region subRegion;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(length = 500)
    private String thumbnail;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "JSON")
    private String amenities; // 편의시설 정보(JSON 문자열 저장)

    @Column(name = "check_in_time")
    private Time checkInTime;
    @Column(name = "check_out_time")
    private Time checkOutTime;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating; // 평균 평점

    @Column(name = "min_price", precision = 10, scale = 2)
    private BigDecimal minPrice; // 최저 가격

    @Column(length = 20)
    private String status; // 운영 상태(active, inactive
}
