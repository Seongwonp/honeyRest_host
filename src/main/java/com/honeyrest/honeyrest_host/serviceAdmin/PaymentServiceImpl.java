package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.PaymentDTO;
import com.honeyrest.honeyrest_host.entity.Payment;
import com.honeyrest.honeyrest_host.repositoryAdmin.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CompanyService companyService;

    // 기본적으로 보여줄 결제 상태(결제완료 + 환불)
    private static final List<String> DEFAULT_STATUSES =
            List.of("DONE", "CANCEL" ); // 결제대기, 결제완료, 결제취소, 환불완료


    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDTO> listForCompanyUser(String loginEmail, Long accommodationId, List<String> statuses, List<String> methods, String q, LocalDate from, LocalDate to, Pageable pageable) {
        // 1) 로그인 유지 + 회사 찾기
       CompanyDTO companyDTO = companyService.getByUserEmail(loginEmail);
        Integer companyId = (companyDTO != null) ? companyDTO.getCompanyId() : null;

        // 2) 결제
        List<String> effStatuses = (statuses == null || statuses.isEmpty()) ? DEFAULT_STATUSES : statuses;
        boolean statusesEmpty = (effStatuses == null || effStatuses.isEmpty());

        // 3) 결제 수단
        List<String> effMethods = (methods == null) ? Collections.emptyList() : methods;
        boolean methodsEmpty = (methods == null || methods.isEmpty());

        // 4) 날짜
        LocalDateTime fromDt = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toExclusive = (to != null) ? to.plusDays(1).atStartOfDay() : null;

        // 5) 레포지토리 호출
        Page<Payment> page = paymentRepository.search(
                companyId,
                accommodationId,
                effMethods, methodsEmpty,
                effStatuses, statusesEmpty,
                fromDt, toExclusive,  q, pageable
        );
        // 6) 엔티티 -> dto 반환
        return page.map(PaymentDTO::of);
    }
}
