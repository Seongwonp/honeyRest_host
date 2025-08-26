package com.honeyrest.honeyrest_host.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dto.accommodation.*;
import com.honeyrest.honeyrest_host.entity.*;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import com.honeyrest.honeyrest_host.repository.RegionRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final CompanyRepository companyRepository;
    private final RegionRepository regionRepository;
    private final AccommodationCategoryRepository accommodationCategoryRepository;
    private final AccommodationTagRepository accommodationTagRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final AccommodationTagMapRepository accommodationTagMapRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    /* ---------------------- JSON 헬퍼 ---------------------- */
    private JsonNode stringToJsonNode(String json) {
        try {
            if (json == null || json.isBlank()) return objectMapper.readTree("[]");
            return objectMapper.readTree(json);
        } catch (Exception e) {
            try {
                return objectMapper.readTree("[]");
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private String jsonNodeToString(JsonNode node) {
        try {
            if (node == null || node.isNull()) return "[]";
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            return "[]";
        }
    }

    /* ---------------------- 매핑: Entity -> Response ---------------------- */
    private AccommodationCreateRequestDTO toResponse(
            Accommodation e,
            List<AccommodationImage> images,
            List<AccommodationTagMap> tagMaps
    ) {
        return AccommodationCreateRequestDTO.builder()
                .accommodationId(e.getAccommodationId())
                .companyId(e.getCompany() != null ? e.getCompany().getCompanyId() : null)
                .categoryId(e.getCategory() != null ? e.getCategory().getCategoryId() : null)
                .mainRegionId(e.getMainRegion() != null ? e.getMainRegion().getRegionId() : null)
                .subRegionId(e.getSubRegion() != null ? e.getSubRegion().getRegionId() : null)
                .name(e.getName())
                .address(e.getAddress())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .thumbnailUrl(e.getThumbnail())
                .description(e.getDescription())
                .amenities(e.getAmenities())
                .checkInTime(e.getCheckInTime())
                .checkOutTime(e.getCheckOutTime())
                .status(e.getStatus())
                .rating(e.getRating())
                .minPrice(e.getMinPrice())
                .images(images == null ? List.of() :
                        images.stream().map(img -> AccommodationImageDTO.builder()
                                        .imageId(img.getImageId())
                                        .imageUrl(img.getImageUrl())
                                        .imageType(img.getImageType())
                                        .sortOrder(img.getSortOrder())
                                        .build()
                                )

                                .toList())
                .tags(
                        tagMaps == null ? List.of() : tagMaps.stream().map(m ->
                                {
                                    AccommodationTag t = m.getTag();
                                    return AccommodationTagMapDTO.builder()
                                            .tagId(t.getTagId())
                                            .name(t.getName()).category(t.getCategory())
                                            .build();
                                })
                                .toList())
                .build();
    }
    // 등록/수정 폼에 바인딩할 때 쓰는 DTO
    private AccommodationCreateRequestDTO toCreateFormDTO(Accommodation e) {
        String thumb = accommodationImageRepository
                .findFirstByAccommodation_AccommodationIdAndImageTypeOrderBySortOrderAscImageIdAsc(
                        e.getAccommodationId(), "MAIN"
                )
                .map(AccommodationImage::getImageUrl)
                .orElse(e.getThumbnail());

        return AccommodationCreateRequestDTO.builder()
                .accommodationId(e.getAccommodationId())
                .companyId(e.getCompany() != null ? e.getCompany().getCompanyId() : null)
                .categoryId(e.getCategory() != null ? e.getCategory().getCategoryId() : null)
                .mainRegionId(e.getMainRegion() != null ? e.getMainRegion().getRegionId() : null)
                .subRegionId(e.getSubRegion() != null ? e.getSubRegion().getRegionId() : null)
                .name(e.getName())
                .address(e.getAddress())
                .thumbnailUrl(thumb)
                .minPrice(e.getMinPrice())
                .status(e.getStatus())
                .build();
    }

    private AccommodationListDTO toListDTOWithThumbnail(Accommodation e) {
        String thumb = accommodationImageRepository
                .findFirstByAccommodation_AccommodationIdAndImageTypeOrderBySortOrderAscImageIdAsc(
                        e.getAccommodationId(), "MAIN"
                )
                .map(AccommodationImage::getImageUrl)
                .orElse(e.getThumbnail()); // MAIN 없으면 엔티티 thumbnail 필드 사용

        return AccommodationListDTO.builder()
                .accommodationId(e.getAccommodationId())
                .name(e.getName())
                .categoryName(e.getCategory().getName())
                .regionName(e.getMainRegion().getName())
                .thumbnailUrl(thumb)     // ★ 리스트에서 썸네일로 사용
                .minPrice(e.getMinPrice())
                .status(e.getStatus())
                .build();

    }


    /* ---------------------- 조회 보조 ---------------------- */
    private Accommodation getEntityOrThrow(Long id) {
        return accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("숙소가 존재하지 않습니다. id=" + id));
    }

    private List<AccommodationImage> findImages(Long accId) {
        return accommodationImageRepository.findByAccommodation_AccommodationIdOrderBySortOrderAsc(accId);
    }

    private List<AccommodationTagMap> findTagMaps(Long accId) {
        return accommodationTagMapRepository.findByAccommodationAccommodationId(accId);
    }

    /* ---------------------- 서비스 구현 ---------------------- */
    @Override
    public List<AccommodationCreateRequestDTO> getAll() {
        return accommodationRepository.findAll().stream()
                .map(e -> toResponse(e, List.of(), List.of()))
                .toList();
    }

    @Override
    public AccommodationCreateRequestDTO getById(Long id) {
        Accommodation e = getEntityOrThrow(id);
        return toResponse(e, findImages(id), findTagMaps(id));
    }

    @Override
    public AccommodationCreateRequestDTO create(AccommodationCreateRequestDTO req) {
        // 필수값 체크
        if (req.getCompanyId() == null || req.getCategoryId() == null ||
            req.getMainRegionId() == null || req.getSubRegionId() == null ||
            req.getName() == null || req.getAddress() == null) {
            throw new IllegalArgumentException("companyId, categoryId, mainRegionId, subRegionId, name, address 는 필수 입니다.");
        }

        // 엔티티 생성
        Accommodation entity = Accommodation.builder()
                .company(companyRepository.getReferenceById(req.getCompanyId()))
                .category(accommodationCategoryRepository.getReferenceById(req.getCategoryId()))
                .mainRegion(regionRepository.getReferenceById(req.getMainRegionId()))
                .subRegion(regionRepository.getReferenceById(req.getSubRegionId()))
                .name(req.getName())
                .address(req.getAddress())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .thumbnail(req.getThumbnailUrl())
                .description(req.getDescription())
                .amenities(req.getAmenities())
                .checkInTime(req.getCheckInTime())
                .checkOutTime(req.getCheckOutTime())
                .status(req.getStatus() == null ? "PENDING" : req.getStatus())
                .minPrice(req.getMinPrice())
                .build();

        Accommodation saved = accommodationRepository.save(entity);

        // 이미지 저장(선택)
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            int idx = 0;
            for (AccommodationImageDTO img : req.getImages()) {
                accommodationImageRepository.save(
                        AccommodationImage.builder()
                                .accommodation(saved)
                                .imageUrl(img.getImageUrl())
                                .imageType(img.getImageType())
                                .sortOrder(img.getSortOrder() != null ? img.getSortOrder() : idx++)
                                .build()
                );
            }
        }

        // 태그 매핑 저장(선택)
        if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
            for (Long tagId : req.getTagIds()) {
                accommodationTagMapRepository.save(
                        AccommodationTagMap.builder()
                                .accommodation(saved)
                                .tag(accommodationTagRepository.getReferenceById(tagId))
                                .build()
                );
            }
        }

        return getById(saved.getAccommodationId()); // 이미지/태그 포함 응답
    }
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<AccommodationListDTO> findByManagerEmail(String email, Pageable pageable) {
        Long companyId = companyRepository.findByEmail(email)
                .map(Company::getCompanyId)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일로 등록된 없체가 없습니다." + email));

        Page<AccommodationListDTO> page = accommodationRepository.findListByCompanyId(companyId, pageable);
        // main 썸네일 주입 로직 필요하면 여기에 추가하기
        return page;

    }

    @Override
    public AccommodationCreateRequestDTO update(Long id, AccommodationUpdateRequestDTO req) {
        Accommodation cur = getEntityOrThrow(id);

        Accommodation updated = Accommodation.builder()
                .accommodationId(cur.getAccommodationId())
                .company(req.getCompanyId() != null ? companyRepository.getReferenceById(req.getCompanyId()) : cur.getCompany())
                .category(req.getCategoryId() != null ? accommodationCategoryRepository.getReferenceById(req.getCategoryId()) : cur.getCategory())
                .mainRegion(req.getMainRegionId() != null ? regionRepository.getReferenceById(req.getMainRegionId()) : cur.getMainRegion())
                .subRegion(req.getSubRegionId() != null ? regionRepository.getReferenceById(req.getSubRegionId()) : cur.getSubRegion())
                .name(req.getName() != null ? req.getName() : cur.getName())
                .address(req.getAddress() != null ? req.getAddress() : cur.getAddress())
                .latitude(req.getLatitude() != null ? req.getLatitude() : cur.getLatitude())
                .longitude(req.getLongitude() != null ? req.getLongitude() : cur.getLongitude())
                .thumbnail(req.getThumbnailUrl() != null ? req.getThumbnailUrl() : cur.getThumbnail())
                .description(req.getDescription() != null ? req.getDescription() : cur.getDescription())
                .amenities(req.getAmenities() != null ? (req.getAmenities()) : cur.getAmenities())
                .checkInTime(req.getCheckInTime() != null ? req.getCheckInTime() : cur.getCheckInTime())
                .checkOutTime(req.getCheckOutTime() != null ? req.getCheckOutTime() : cur.getCheckOutTime())
                .status(req.getStatus() != null ? req.getStatus() : cur.getStatus())
                .rating(cur.getRating())
                .minPrice(req.getMinPrice() != null ? req.getMinPrice() : cur.getMinPrice())
                .build();

        accommodationRepository.save(updated);

        // 정책: 이미지/태그 “덮어쓰기” 요청 시에만 교체
        if (req.getImages() != null) {
            accommodationImageRepository.deleteByAccommodation_AccommodationId(id);
            int idx = 0;
            for (AccommodationImageDTO img : req.getImages()) {
                accommodationImageRepository.save(
                        AccommodationImage.builder()
                                .accommodation(updated)
                                .imageUrl(img.getImageUrl())
                                .imageType(img.getImageType())
                                .sortOrder(img.getSortOrder() != null ? img.getSortOrder() : idx++)
                                .build()
                );
            }
        }


        if (req.getTagIds() != null) {
            accommodationTagMapRepository.deleteByAccommodationAccommodationId(id);
            Accommodation accRef = accommodationRepository.getReferenceById(id);
            for (Long tagId : req.getTagIds()) {
                AccommodationTag tagRef = accommodationTagRepository.getReferenceById(tagId);
                accommodationTagMapRepository.save(
                        AccommodationTagMap.builder()
                                .accommodation(accRef)
                                .tag(tagRef)
                                .build()
                );
            }
        }

        return getById(id);
    }

    @Override
    @Transactional // 삭제 메서드가 업데이트,삭제 쿼리를 실행하므로 트랜잭션이 필요함
    public void delete(Long id) {
        // 연관 데이터 먼저 삭제
        accommodationImageRepository.deleteByAccommodation_AccommodationId(id);
        accommodationTagMapRepository.deleteByAccommodationAccommodationId(id);
        // 마지막에 본체 삭제
        accommodationRepository.deleteById(id);
    }

    @Override
    public Page<AccommodationListDTO> search(String q, Long categoryId, Long mainRegionId, Pageable pageable) {
        return accommodationRepository.search(q, categoryId, mainRegionId, pageable);
    }

    @Override
    public void changeStatus(Long id, String status) {
        var e = accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("숙소가 존재하지 않습니다. id=" + id));

        // 빌더 패턴이라 세터가 없으니, 동일 PK로 새로 빌드해서 상태만 바꿔 저장
        var updated = Accommodation.builder()
                .accommodationId(e.getAccommodationId())
                .company(e.getCompany())
                .category(e.getCategory())
                .mainRegion(e.getMainRegion())
                .subRegion(e.getSubRegion())
                .name(e.getName())
                .address(e.getAddress())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .thumbnail(e.getThumbnail())
                .description(e.getDescription())
                .amenities(e.getAmenities())
                .checkInTime(e.getCheckInTime())
                .checkOutTime(e.getCheckOutTime())
                .status(status)               // ← 여기만 변경
                .rating(e.getRating())
                .minPrice(e.getMinPrice())
                .build();

        accommodationRepository.save(updated);
    }

    public long count() {
        return accommodationRepository.count(); // JpaRepository 기본 제공
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<AccommodationListDTO> findByCompanyId(Long companyId, Pageable pageable) {
        Page<AccommodationListDTO> page = accommodationRepository.findListByCompanyId(companyId, pageable);

        // 페이지 내용이 비어있으면 바로 반환
        if (page.isEmpty()) return page;

        // 1) 현재 페이지의 숙소 id들
        List<Long> ids = page.stream()
                .map(AccommodationListDTO::getAccommodationId)
                .toList();

        // 2) id들에 대한 MAIN 이미지들을 한 번에 조회
        var mains = accommodationImageRepository
                .findByAccommodation_AccommodationIdInAndImageTypeOrderBySortOrderAscImageIdAsc(ids, "MAIN");

        // 3) id -> url 맵
        Map<Long, String> thumbMap = mains.stream().collect(
                java.util.stream.Collectors.toMap(
                        img -> img.getAccommodation().getAccommodationId(),
                        AccommodationImage::getImageUrl,
                        (a, b) -> a // 중복 시 첫 번째 유지
                )
        );

        // 4) DTO에 썸네일 주입 (없으면 null 유지 → 템플릿에서 '없음' 처리)
        List<AccommodationListDTO> content = page.stream().map(dto -> {
            dto.setThumbnailUrl(thumbMap.get(dto.getAccommodationId()));
            return dto;
        }).toList();

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public Page<AccommodationListDTO> findByCategoryIdAndStatus(Long companyId, String status, Pageable pageable) {
        return accommodationRepository.findByCompany_CompanyIdAndStatus(companyId, status,pageable)
                .map(this::toListDTOWithThumbnail);
    }

}
