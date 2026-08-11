package com.honeyrest.honeyrest_host.repositoryAdmin.accommodation;

import com.honeyrest.honeyrest_host.entity.AccommodationImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccommodationImageRepository extends JpaRepository<AccommodationImage, Long> {

    // 단건(리스트 썸네일용): MAIN을 sortOrder→imageId 순으로 첫 1건
    Optional<AccommodationImage>
    findFirstByAccommodation_AccommodationIdAndImageTypeOrderBySortOrderAscImageIdAsc(
            Long accommodationId,String imageType
    );

    // 배치 조회(페이지에 뜬 N개 숙소의 MAIN 썸네일 한 번에) -> 여러 숙소의 main들 한꺼번에
    List<AccommodationImage>
    findByAccommodation_AccommodationIdInAndImageTypeOrderBySortOrderAscImageIdAsc(
            List<Long> accommodationIds, String imageType
    );
    // 단일 숙소 + 타입별 목록
    List<AccommodationImage>
    findByAccommodation_AccommodationIdAndImageTypeOrderBySortOrderAscImageIdAsc(
            Long accommodationId, String imageType
    );

    // 상세 이미지 목록
    List<AccommodationImage>
    findByAccommodation_AccommodationIdOrderBySortOrderAsc(Long accommodationId);

    // 삭제
    void deleteByAccommodation_AccommodationId(Long accommodationId);

    void deleteByAccommodation_AccommodationIdAndImageType(Long accommodationId, String imageType);

    // 이미지가 실제로 이 숙소 소속일 때만 삭제(다른 숙소의 imageId를 섞어 보내는 것을 방지, P1-4)
    void deleteByAccommodation_AccommodationIdAndImageIdIn(Long accommodationId, List<Long> imageIds);

}