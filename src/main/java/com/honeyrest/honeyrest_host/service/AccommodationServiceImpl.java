package com.honeyrest.honeyrest_host.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dto.accommodation.*;

import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.AccommodationImage;
import com.honeyrest.honeyrest_host.entity.AccommodationTag;
import com.honeyrest.honeyrest_host.entity.AccommodationTagMap;
import com.honeyrest.honeyrest_host.entity.enums.OperationStatus;
import com.honeyrest.honeyrest_host.repository.accommodation.*;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import com.honeyrest.honeyrest_host.repository.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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


    /* ------ json -> String ---------*/
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

    /* ---------------------- 유틸: 시간 변환 ---------------------- */
    // Entity는 LocalDateTime(컬럼 TIME이 아니고 DATETIME)이므로 기준일 붙여 저장
    private static final LocalDateTime EPOCH_DAY = LocalDateTime.of(1970, 1, 1, 0, 0);

    private LocalDateTime toDateTime(LocalTime t) {
        return (t == null) ? null : EPOCH_DAY.withHour(t.getHour()).withMinute(t.getMinute());
    }

    private LocalTime toTime(LocalDateTime dt) {
        return (dt == null) ? null : dt.toLocalTime();
    }

    /* ---------------------- 매핑: Entity -> Response ---------------------- */
    private AccommodationResponseDTO toResponse(Accommodation e,
                                                List<AccommodationImage> images,
                                                List<AccommodationTagMap> tagMaps) {
        return AccommodationResponseDTO.builder()
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
                .amenities(stringToJsonNode(e.getAmenities()))
                .checkInTime((e.getCheckInTime()))
                .checkOutTime((e.getCheckOutTime()))
                .status(String.valueOf(e.getStatus()))
                .rating(e.getRating())
                .minPrice(e.getMinPrice())
                .images(images == null ? List.of() :
                        images.stream().map(img -> new AccommodationImageDTO(
                                img.getImageId(),
                                img.getImageUrl(),
                                img.getImageType(),
                                img.getSortOrder()
                        )).toList())
                .tags(tagMaps == null ? List.of() :
                        tagMaps.stream().map(m -> {
                            AccommodationTag t = m.getTag();
                            return new AccommodationTagMapDTO(t.getTagId(), t.getName(), t.getCategory());
                        }).toList())
                .build();
    }

    /* ---------------------- 조회 보조 ---------------------- */
    private Accommodation getEntityOrThrow(Long id) {
        return accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("숙소가 존재하지 않습니다. id=" + id));
    }

    private List<AccommodationImage> findImages(Long accId) {
        return accommodationImageRepository.findByAccommodationAccommodationIdOrderBySortOrderAsc(accId);
    }

    private List<AccommodationTagMap> findTagMaps(Long accId) {
        return accommodationTagMapRepository.findByAccommodationAccommodationId(accId);
    }


    @Override
    @Transactional
    public List<AccommodationResponseDTO> getAll() {
        List<Accommodation> accommodations = accommodationRepository.findAll();
        return accommodations.stream()
                .map(e -> toResponse(e, List.of(), List.of()))
                .toList();
    }

    @Override
    public AccommodationResponseDTO getById(Long id) {
        Accommodation e = getEntityOrThrow(id);
        return toResponse(e, findImages(id), findTagMaps(id));
    }

    @Override
    public AccommodationResponseDTO create(AccommodationCreateRequestDTO req) {
        if (req.getCompanyId() == null || req.getCategoryId() == null ||
                req.getMainRegionId() == null || req.getSubRegionId() == null ||
                req.getName() == null || req.getAddress() == null) {
            throw new IllegalArgumentException("companyId, categoryId, mainRegionId, subRegionId, name, address 는 필수 입니다. ");
        }
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
                .amenities(jsonNodeToString(req.getAmenities()))
                .checkInTime((req.getCheckInTime()))
                .checkOutTime((req.getCheckOutTime()))
                .status(req.getStatus() == null ? OperationStatus.ACTIVE : req.getStatus())
                .minPrice(req.getMinPrice())
                .build();

        Accommodation saved = accommodationRepository.save(entity);

        // 이미지 저장(선택: 전체 신규)
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            int idx = 0;
            for (AccommodationImageDTO img : req.getImages()) {
                AccommodationImage row = AccommodationImage.builder()
                        .accommodation(saved)
                        .imageUrl(img.getImageUrl())
                        .imageType(img.getImageType())
                        .sortOrder(img.getSortOrder() != null ? img.getSortOrder() : idx++)
                        .build();
                accommodationImageRepository.save(row);
            }
        }

        // 태그 매핑 저장(선택: 초기 매핑)
        if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
            for (Long tagId : req.getTagIds()) {
                AccommodationTag tag = accommodationTagRepository.getReferenceById(tagId);
                AccommodationTagMap map = AccommodationTagMap.builder()
                        .accommodation(saved)
                        .tag(tag)
                        .build();
                accommodationTagMapRepository.save(map);
            }
        }

        return getById(saved.getAccommodationId()); // 이미지/태그 포함 응답
    }

    @Override
    public AccommodationResponseDTO update(Long id, AccommodationUpdateRequestDTO req) {
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
                .amenities(req.getAmenities() != null ? jsonNodeToString(req.getAmenities()) : cur.getAmenities())
                .checkInTime(req.getCheckInTime() != null ? (req.getCheckInTime()) : cur.getCheckInTime())
                .checkOutTime(req.getCheckOutTime() != null ? (req.getCheckOutTime()) : cur.getCheckOutTime())
                .status(req.getStatus() != null ? req.getStatus() : cur.getStatus())
                .rating(cur.getRating())
                .minPrice(req.getMinPrice() != null ? req.getMinPrice() : cur.getMinPrice())
                .build();

        accommodationRepository.save(updated);

        // (정책) 이미지/태그 덮어쓰기 예시
        if (req.getImages() != null) {
            accommodationImageRepository.deleteByAccommodationAccommodationId(id);
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
            for (Long tagId : req.getTagIds()) {
                accommodationTagMapRepository.save(
                        AccommodationTagMap.builder()
                                .accommodation(updated)
                                .tag(accommodationTagRepository.getReferenceById(tagId))
                                .build()
                );
            }
        }

        return getById(id);

    }


    @Override
    public void delete(Long id) {
        // 이미지/태그 매핑 선삭제(필요 시)
        accommodationImageRepository.deleteByAccommodationAccommodationId(id);
        accommodationTagMapRepository.deleteByAccommodationAccommodationId(id);
        accommodationRepository.deleteById(id);
    }

    @Override
    public Page<AccommodationListDTO> search(String q, Long categoryId, Long mainRegionId, Pageable pageable) {
        return accommodationRepository.search(q, categoryId, mainRegionId, pageable);
    }
}
