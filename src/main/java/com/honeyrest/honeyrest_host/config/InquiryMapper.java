package com.honeyrest.honeyrest_host.config;

import com.honeyrest.honeyrest_host.dto.InquiryDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.Inquiry;
import com.honeyrest.honeyrest_host.entity.User;
import org.springframework.stereotype.Component;

@Component
public class InquiryMapper {


    public InquiryDTO toDTO(Inquiry e) {
        if (e == null) return null;
        return InquiryDTO.builder()
                .inquiryId(e.getInquiryId())
                .userId(e.getUser() != null ? e.getUser().getUserId() : null)
                .userName(e.getUser() != null ? e.getUser().getName() : null)
                .accommodationId(e.getAccommodation() != null ? e.getAccommodation().getAccommodationId() : null)
                .accommodationName(e.getAccommodation() != null ? e.getAccommodation().getName() : null)
                .title(e.getTitle())
                .content(e.getContent())
                .reply(e.getReply())
                .isReplied(Boolean.TRUE.equals(e.getIsReplied()))
                .category(e.getCategory())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    /** 생성: 엔티티를 '빌더'로 만들어 반환 (연관관계는 서비스에서 주입) */
    public Inquiry toEntityForCreate(InquiryDTO dto, User user, Accommodation acc) {
        return Inquiry.builder()
                .user(user)
                .accommodation(acc)
                .title(dto.getTitle())
                .content(dto.getContent())
                .reply(dto.getReply())
                .isReplied(dto.getReply() != null && !dto.getReply().isBlank())
                .category(dto.getCategory())
                .build();
    }

    /** 수정: 엔티티의 도메인 메서드를 호출해서 변경 (더티체킹) */
    public void applyForUpdate(Inquiry target, InquiryDTO dto) {
        if (dto.getTitle()    != null) target.changeTitle(dto.getTitle());
        if (dto.getContent()  != null) target.changeContent(dto.getContent());
        if (dto.getCategory() != null) target.changeCategory(dto.getCategory());
        if (dto.getReply()    != null) target.changeReply(dto.getReply()); // isReplied까지 동기화
    }
}
