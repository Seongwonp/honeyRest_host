package com.honeyrest.honeyrest_host.dtoOwner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDTO {
    private Integer companyId;          // 업체 고유 식별자 (PK)
    private String name;             // 업체명
    private String businessNumber;   // 사업자 등록번호
    private String ownerName;        // 대표자명
    private String phone;            // 대표 연락처
    private String email;            // 대표 이메일
    private String address;          // 사업장 주소
    private LocalDateTime createdAt;

    // bank_info JSON → Map 형태로 매핑
    private String bankInfo; // 은행명, 계좌번호, 예금주 등

    private BigDecimal commissionRate;   // 수수료율
    private String status;           // 승인 상태 (PENDING, APPROVED, REJECTED)
}
