// AccommodationRequestMap 엔티티는 숙소 요청과 관련된 정보를 저장하며,
// 숙소, 태그 맵, 업체와의 관계를 매핑합니다.
// 요청 설명, 상태, 서류 파일 URL 등의 필드를 포함하여
// 숙소 요청의 상세 정보를 관리하기 위한 설계입니다.
// DTO에서 리스트 형태로 데이터를 받아 처리할 수 있도록 설계

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
@Table(name = "accommodation_request_map")
public class AccommodationRequestMap extends BaseEntity {

    // 기본 키(PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requestmap_id")
    private Long RequestMapId;

    // 숙소 엔티티와의 다대일 관계를 나타냄
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    // 태그 맵 엔티티와의 다대일 관계를 나타냄
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id", nullable = false)
    private AccommodationTagMap tagMap;

    // 업체(회사) 엔티티와의 다대일 관계를 나타냄
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // 요청에 대한 설명을 저장하는 필드
    @Column(name = "description")
    private String description;

    // 요청의 상태를 저장하는 필드
    @Column(name = "status")
    private String status;


    // 서류 파일 URL 정보를 저장하는 필드
    @Column(name = "file_url")
    private String fileUrl;
}

