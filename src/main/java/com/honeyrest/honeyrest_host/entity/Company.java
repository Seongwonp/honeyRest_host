package com.honeyrest.honeyrest_host.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company")
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Integer companyId; // 업체 고유 식별자

    @Column(name = "name", nullable = false, length = 200)
    private String name; // 업체명

    @Column(name = "business_number", nullable = false, length = 50)
    private String businessNumber; // 사업자 등록번호

    @Column(name = "owner_name",length = 20)
    private String ownerName; // 대표자명

    @Column(name = "phone", length = 20)
    private String phone; // 대표 연락처

    @Column(name = "email", length = 100)
    private String email; // 대표 이메일

    @Column(name = "address", length = 255)
    private String address; // 사업장 주소

    @Column(name = "bank_info", columnDefinition = "JSON")
    private String bankInfo; // 은행명

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate; // 수수료율

    @Column(name = "status", length = 20)
    private String status; // 승인 상태

}
