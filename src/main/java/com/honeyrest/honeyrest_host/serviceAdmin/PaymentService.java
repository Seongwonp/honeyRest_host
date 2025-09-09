package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.PaymentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
    // 회사 관리자가 볼 수 있는 결제 내역
    Page<PaymentDTO> listForCompanyUser(
            String loginEmail,
            Long accommodationId,
            List<String> statuses,
            List<String> methods,
            String q,
            LocalDate from,
            LocalDate to,
            Pageable pageable);


}
