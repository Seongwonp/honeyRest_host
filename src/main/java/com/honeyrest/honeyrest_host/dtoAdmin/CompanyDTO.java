package com.honeyrest.honeyrest_host.dtoAdmin;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private Integer companyId; // 업체 고유 식별자


    @NotBlank(message = "업체명은 필수입니다.")
    private String name; // 업체명

    @NotBlank(message = "사업자 등록번호는 필수입니다.")
    private String businessNumber; // 사업자 등록번호


    @NotBlank(message = "대표자명은 필수입니다.")
    private String ownerName; // 대표자명


    @NotBlank(message = "대표 연락처는 필수입니다.")
    private String phone; // 대표 연락처


    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email; // 대표 이메일


    private String address; // 사업장 주소


    private String bankInfo; // 은행명

    @DecimalMin(value = "0.0", inclusive = true, message = "수수료율은 0 이상이어야 합니다.")
    private BigDecimal commissionRate; // 수수료율


    private String status; // 승인 상태

}
