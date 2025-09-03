package com.honeyrest.honeyrest_host.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "cancellation_policy")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancellationPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    private String policyName;

    @Column(columnDefinition = "JSON")
    private String detail; // JSON 문자열로 저장 ,환불 규정 상세
}