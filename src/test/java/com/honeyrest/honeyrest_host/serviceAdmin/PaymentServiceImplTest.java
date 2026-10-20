package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.repositoryAdmin.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 로그인 이메일로 회사를 특정할 수 없으면 전체 결제 내역 대신 빈 결과를 반환해야 한다(fail-closed).
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void companyId를_확인할_수_없으면_빈_결과를_반환하고_repository를_호출하지_않는다() {
        when(companyService.getByUserEmail(null)).thenReturn(null);

        Page<?> result = paymentService.listForCompanyUser(
                null, null, null, null, null, null, null, PageRequest.of(0, 10));

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        verifyNoInteractions(paymentRepository);
    }
}
