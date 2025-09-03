package com.honeyrest.honeyrest_host.serviceAdmin;


import com.honeyrest.honeyrest_host.config.InquiryMapper;
import com.honeyrest.honeyrest_host.dto.InquiryDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.Inquiry;
import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.repositoryAdmin.InquiryRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class InquiryServiceImpl implements InquiryService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryMapper inquiryMapper;


    @Override
    public InquiryDTO create(InquiryDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Accommodation acc = null;
        if (dto.getAccommodationId() != null) {
            acc = accommodationRepository.findById(dto.getAccommodationId())
                    .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));
        }

        Inquiry entity = inquiryMapper.toEntityForCreate(dto, user, acc);
        Inquiry saved = inquiryRepository.save(entity);
        return inquiryMapper.toDTO(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public InquiryDTO get(Long inquiryId) {
        Inquiry entity = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다. id=" + inquiryId));
        return inquiryMapper.toDTO(entity);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public Page<InquiryDTO> listByUser(Long userId, Pageable pageable) {
//        return inquiryRepository.f(userId, pageable)
//                .map(inquiryMapper::toDTO);
//    }

    @Override
    @Transactional(readOnly = true)
    public Page<InquiryDTO> listByAccommodation(Long accommodationId, Pageable pageable) {
        return inquiryRepository.findByAccommodation(accommodationId, pageable)
                .map(inquiryMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InquiryDTO> listByCompany(Long companyId, String q, Boolean replied, Pageable pageable) {
        // 공백/빈 문자열 방지
        String keyword = (q != null && !q.isBlank()) ? q.trim() : null;
        return inquiryRepository.searchByCompany(companyId, keyword, replied, pageable)
                .map(inquiryMapper::toDTO);
    }

    /* 관리자 답변 등록/수정 */
    @Override
    public InquiryDTO reply(Long inquiryId, String replyText) {
        Inquiry entity = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다. id=" + inquiryId));

        entity.changeReply(replyText); // isReplied 동기화 포함
        // @Transactional 이므로 flush 시점에 UPDATE
        return inquiryMapper.toDTO(entity);
    }

    /* 제목/ 내용/ 카테고리 일반 수정 */
    @Override
    public InquiryDTO update(Long inquiryId, InquiryDTO dto) {
        Inquiry entity = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다. id=" + inquiryId));


        if (dto.getTitle() != null) entity.changeTitle(dto.getTitle());
        if (dto.getContent() != null) entity.changeContent(dto.getContent());
        if (dto.getReply() != null) entity.changeReply(dto.getReply());
        if (dto.getCategory() != null) entity.changeCategory(dto.getCategory());

        // 제목/내용/카테고리/답변 등 변경
        inquiryMapper.applyForUpdate(entity, dto);
        return inquiryMapper.toDTO(entity);
    }

    @Override
    public void delete(Long inquiryId) {
        if (!inquiryRepository.existsById(inquiryId)) return;
        inquiryRepository.deleteById(inquiryId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<InquiryDTO> listByCompany(Long companyId, Long accId, String q, Boolean replied, Pageable pageable) {
        Page<Inquiry> page = inquiryRepository.findByCompanyWithFilters(companyId, accId, q, replied, pageable);
        return page.map(inquiryMapper::toDTO);
    }
}

