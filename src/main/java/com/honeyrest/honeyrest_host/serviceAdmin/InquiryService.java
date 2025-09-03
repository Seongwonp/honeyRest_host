package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dto.InquiryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface InquiryService {
    InquiryDTO create(InquiryDTO dto);

    InquiryDTO get(Long inquiryId);

//    Page<InquiryDTO> listByUser(Long userId, Pageable pageable);

    Page<InquiryDTO> listByAccommodation(Long accommodationId, Pageable pageable);

    Page<InquiryDTO> listByCompany(Long companyId, String q, Boolean replied, Pageable pageable);

    /** 관리자 답변 등록/수정 → isReplied 자동 갱신 */
    InquiryDTO reply(Long inquiryId, String replyText);

    InquiryDTO update(Long inquiryId, InquiryDTO dto);

    void delete(Long inquiryId);

    @Transactional(readOnly = true)
    Page<InquiryDTO> listByCompany(Long companyId, Long accId, String q, Boolean replied, Pageable pageable);
}
