package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;

import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationImageDTO;
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

    // 메인 썸네일(MAIN) 행 삭제 + 숙소 엔티티 thumbnail 비우기. 지운 이미지 URL 을 돌려준다(파일은 호출자가 커밋 후 삭제)
    List<String> deleteMainThumbnail(Long accommodationId);

    // 여러장 삭제 (accommodationId 소속인 이미지만, P1-4)
    void deleteSubImages(Long accommodationId, List<Long> imageIds);
    // 정렬 변경 옵션
    void updateSort(Long ImageId, Integer sortOrder);

    // 편의 함수 썸네일 메인 사진 1장 보장 업서트
    AccommodationImageDTO upsertMainThumbnail(Long accommodationId, AccommodationImageDTO accommodationImageDTO);

    List<AccommodationImageDTO> getByAccommodation_AccommodationId(Long accommodationId,String imageType);

    //숙소 엔티티의 썸네일 url 동기화
    @Transactional
    void updateThumbnailUrl(Long accommodationId, String thumbnailUrl);
}
