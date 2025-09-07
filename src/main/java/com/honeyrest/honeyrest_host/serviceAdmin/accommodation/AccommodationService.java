package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;

import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationListDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccommodationService {
    // 목록
    List<AccommodationCreateRequestDTO> getAll();

    List<AccommodationCreateRequestDTO> getAllById(Long companyId);

    // id 조회
    AccommodationCreateRequestDTO getById(Long id);

    // 등록
    AccommodationCreateRequestDTO create(AccommodationCreateRequestDTO req);

    // 수정
    AccommodationCreateRequestDTO update(Long id, AccommodationUpdateRequestDTO req);

    // 삭제
    void delete(Long id);

    Page<AccommodationListDTO> search(String q, Long categoryId, Long mainRegionId, Pageable pageable);

    // 승인
    void changeStatus(Long id, String status); // "APPROVED" | "REJECTED" | "ACTIVE" 등

    long count();
    // 회사별 객실 조회
    Page<AccommodationListDTO> findByCompanyId(Long companyId, Pageable pageable);

    // 회사 + 상태별 조회(승인 대기 목록)
    Page<AccommodationListDTO> findByCategoryIdAndStatus(Long companyId, String status, Pageable pageable);

    // 숙소 이름만 필요, 아무것도 말고 숙소명만 가져오기
    String getNameById(Long accommodationId);

    AccommodationCreateRequestDTO getDetail(Long accId);

    // 관리자 이메일을 가지고 숙소 id 를 가져오기 위함
    List<Long> getAccommodationIdsByAdminEmail(String email);
}