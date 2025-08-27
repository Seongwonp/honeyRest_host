package com.honeyrest.honeyrest_host.service.accommodation;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationImageDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccommodationImageService {

    // 단건 업로드(파일 → URL 저장 → 엔티티 저장)
    AccommodationImageDTO saveOrUpload(Long accommodationId, AccommodationImageDTO dto);


    @Transactional(readOnly = true)
    List<AccommodationImageDTO> getImages(Long accommodationId);

    // main 썸네일 조회
    AccommodationImageDTO getMainImage(Long accommodationId,String imageType);

    //삭제
    void delete(Long imageId);
    // 정렬 변경 옵션
    void updateSort(Long ImageId, Integer sortOrder);

    // 편의 함수 썸네일 메인 사진 1장 보장 업서트
    AccommodationImageDTO upsertMainThumbnail(Long accommodationId, AccommodationImageDTO accommodationImageDTO);

    List<AccommodationImageDTO> getByAccommodation_AccommodationId(Long accommodationId,String imageType);
}
