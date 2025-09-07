package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.*;
import com.honeyrest.honeyrest_host.entity.*;
import com.honeyrest.honeyrest_host.repositoryAdmin.CancellationPolicyRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.CompanyRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RegionRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.*;
import com.honeyrest.honeyrest_host.serviceAdmin.CancellationPolicyService;
import com.honeyrest.honeyrest_host.utilAdmin.AmenitiesParser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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
    private final AccommodationImageService accommodationImageService;
    private final CancellationPolicyRepository cancellationPolicyRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CancellationPolicyService cancellationPolicyService;
    private final AccommodationTagService accommodationTagService;

    /* ---------------------- JSON 헬퍼 ---------------------- */
    private String normalizeJsonList(String raw) {
        return AmenitiesParser.normalizeToJson(raw);
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

    private AccommodationCreateRequestDTO toDto(Accommodation a) {
        return AccommodationCreateRequestDTO.builder()
                .accommodationId(a.getAccommodationId())
                .name(a.getName())
                .companyId(a.getCompany().getCompanyId())
                .mainRegionId(a.getMainRegion().getRegionId())
                .subRegionId(a.getSubRegion().getRegionId())
                .categoryId(a.getCategory().getCategoryId())
                .address(a.getAddress())
                .description(a.getDescription())
                .amenities(a.getAmenities())
                .checkInTime(a.getCheckInTime())
                .checkOutTime(a.getCheckOutTime())
                .status(a.getStatus())
                .minPrice(a.getMinPrice())
                .build();

    }

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
                .thumbnail(e.getThumbnail())
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
                                    return AccommodationTagDTO.builder()
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
                .thumbnail(thumb)
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
                .thumbnail(thumb)     // ★ 리스트에서 썸네일로 사용
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
        return accommodationTagMapRepository.findByAccommodation_AccommodationId(accId);
    }

    /* ---------------------- 서비스 구현 ---------------------- */
    @Override
    public List<AccommodationCreateRequestDTO> getAll() {
        return accommodationRepository.findAll().stream()
                .map(e -> toResponse(e, List.of(), List.of()))
                .toList();
    }

    @Override
    public List<AccommodationCreateRequestDTO> getAllById(Long companyId) {
        return accommodationRepository.findAllByCompany_CompanyId(companyId).stream().map(this::toDto).toList();

    }

    @Override
    public AccommodationCreateRequestDTO getById(Long id) {
        Accommodation a = accommodationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("숙소가 없습니다. id=" + id));

        // --- 태그 로드 (널 세이프) ---
        List<AccommodationTagDTO> tagDTOs =
                Optional.ofNullable(accommodationTagService.findByAccommodationId(id))
                        .orElseGet(Collections::emptyList);

        List<Long> tagIds = tagDTOs.stream()
                .map(AccommodationTagDTO::getTagId)
                .filter(Objects::nonNull)
                .toList();

        // --- 이미지 로드 (메인/서브) + 정렬 (sortOrder 오름차순, null은 마지막) ---
        List<AccommodationImageDTO> imageDTOs =
                Optional.ofNullable(accommodationImageService.getImages(id))
                        .orElseGet(Collections::emptyList);

        List<AccommodationImageDTO> sortedImages = new ArrayList<>(imageDTOs);
        sortedImages.sort(Comparator.comparing(
                AccommodationImageDTO::getSortOrder,
                Comparator.nullsLast(Integer::compareTo)
        ));
        // 주의: List에는 setImages 같은 메서드가 없습니다. 정렬 결과는 빌더의 images(...)로 넘깁니다.

        // --- 취소/환불 규정 (멀티라인 문자열) ---
        String cancelJson = cancellationPolicyService.getMultilineByAccommodationId(id);

        // --- Region/Category/Company 등 ID 안전 추출 ---
        Long companyId   = Optional.ofNullable(a.getCompany()).map(Company::getCompanyId).orElse(null);
        Long categoryId  = Optional.ofNullable(a.getCategory()).map(AccommodationCategory::getCategoryId).orElse(null);
        Long mainRegionId= Optional.ofNullable(a.getMainRegion()).map(Region::getRegionId).orElse(null);

        // 프로젝트마다 필드명이 다를 수 있어요.
        // 엔티티가 getSubRegion() (Region) 을 쓰면 아래처럼:
        Long subRegionId = Optional.ofNullable(a.getSubRegion()).map(Region::getRegionId).orElse(null);
        // 만약 엔티티가 getSubRegionId() (Region) 라면 윗줄 대신 이렇게:
        // Long subRegionId = Optional.ofNullable(a.getSubRegionId()).map(Region::getRegionId).orElse(null);

        return AccommodationCreateRequestDTO.builder()
                .accommodationId(a.getAccommodationId())
                .companyId(companyId)
                .categoryId(categoryId)
                .mainRegionId(mainRegionId)
                .subRegionId(subRegionId)
                .name(a.getName())
                .address(a.getAddress())
                .latitude(a.getLatitude())
                .longitude(a.getLongitude())
                .thumbnail(a.getThumbnail())
                .description(a.getDescription())
                .amenities(a.getAmenities())               // DTO 필드 타입(String/JSON 또는 List)에 맞춰 필요시 변환
                .cancellationPolicyDetail(cancelJson)      // 뷰에서 List로 변환해 사용 예정이면 주석 유지 OK
                .checkInTime(a.getCheckInTime())
                .checkOutTime(a.getCheckOutTime())
                .status(a.getStatus())
                .minPrice(a.getMinPrice())
                .tagIds(tagIds)
                .tags(tagDTOs)
                .images(sortedImages)
                .build();
    }

    @Override
    public AccommodationCreateRequestDTO create(AccommodationCreateRequestDTO req) {
        // 필수값 체크
        if (req.getCompanyId() == null || req.getCategoryId() == null ||
            req.getMainRegionId() == null || req.getSubRegionId() == null ||
            req.getName() == null || req.getAddress() == null) {
            throw new IllegalArgumentException("companyId, categoryId, mainRegionId, subRegionId, name, address 는 필수 입니다.");
        }

        // 문자역 -> json 정규화
        String amenitiesJson = normalizeJsonList(req.getAmenities());
        String cancelPolicyJson = normalizeJsonList(req.getCancellationPolicyDetail());

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
                .thumbnail(req.getThumbnail())
                .description(req.getDescription())
                .amenities(amenitiesJson)
                .checkInTime(req.getCheckInTime())
                .checkOutTime(req.getCheckOutTime())
                .status(req.getStatus() == null ? "PENDING" : req.getStatus())
                .minPrice(req.getMinPrice())
                .build();

        Accommodation saved = accommodationRepository.save(entity);

        /* 메인 이미지 처리 (선택)
           - req.getFile() 에 파일이 오면 메인으로 업로드
           - 아니면 req.getthumbnail() 만 두고 넘어가도 됨
        */

        // 환불 취소 규정, 저장 (별도 서비스/ 테이블이라면 여기서 저장)
        if (req.getCancellationPolicyDetail() != null && !req.getCancellationPolicyDetail().isBlank()) {
            cancellationPolicyService.saveOrUpdate(
                    saved.getAccommodationId(),
                    req.getCancellationPolicyDetail()
            );
        }
        if (req.getFile() != null && !req.getFile().isEmpty()) {
            accommodationImageService.upsertMainThumbnail(
                    saved.getAccommodationId(),
                    AccommodationImageDTO.builder()
                            .imageType("MAIN")
                            .file(req.getFile())
                            .sortOrder(0)
                            .build()
            );
        }

        // 서브 이미지 처리 (파일 or URL 모두 허용)
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            int idx = 1;
            for (AccommodationImageDTO img : req.getImages()) {
                if ((img.getFile() == null || img.getFile().isEmpty())
                    && (img.getImageUrl() == null || img.getImageUrl().isBlank())) {
                    continue; // 진짜로 아무것도 없으면 skip
                }
                if (img.getSortOrder() == null) img.setSortOrder(idx++);
                if (img.getImageType() == null || img.getImageType().isBlank()) img.setImageType("SUB");

                accommodationImageService.saveOrUpload(saved.getAccommodationId(), img);
            }
        }

        // 태그 매핑 (그대로)
        if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
            accommodationTagService.replaceMapping(saved.getAccommodationId(), req.getTagIds());

        }

        return getById(saved.getAccommodationId());
    }


    @Transactional
    public Page<AccommodationListDTO> findByManagerEmail(String email, Pageable pageable) {
        Long companyId = companyRepository.findByEmail(email)
                .map(Company::getCompanyId)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일로 등록된 없체가 없습니다." + email));

        Page<AccommodationListDTO> page = accommodationRepository.findListByCompanyId(companyId, pageable);
        // main 썸네일 주입 로직 필요하면 여기에 추가하기
        return page;

    }

    @Transactional
    @Override
    public AccommodationCreateRequestDTO update(Long id, AccommodationUpdateRequestDTO req) {
        getEntityOrThrow(id); // 존재 여부 확인

        // 연관관계: 변경 요청 있을 때만 각각 호출
        if (req.getCompanyId() != null) {
            accommodationRepository.updateCompany(id, companyRepository.getReferenceById(req.getCompanyId()));
        }
        if (req.getCategoryId() != null) {
            accommodationRepository.updateCategory(id, accommodationCategoryRepository.getReferenceById(req.getCategoryId()));
        }
        if (req.getMainRegionId() != null) {
            accommodationRepository.updateMainRegion(id, regionRepository.getReferenceById(req.getMainRegionId()));
        }
        if (req.getSubRegionId() != null) {
            accommodationRepository.updateSubRegion(id, regionRepository.getReferenceById(req.getSubRegionId()));
        }

        String name = hasText(req.getName()) ? req.getName().trim() : null;
        String address = hasText(req.getAddress()) ? req.getAddress().trim() : null;
        BigDecimal latitude = req.getLatitude();
        BigDecimal longitude = req.getLongitude();

        // DTO가 thumbnail(또는 thumbnailUrl)인지에 맞춰 변수 이름 맞추기
        String thumbnail = hasText(req.getThumbnail()) ? req.getThumbnail().trim() : null;

        String description = hasText(req.getDescription()) ? req.getDescription().trim() : null;

        String amenitiesJson = null;
        if (req.getAmenities() != null) {
            String s = req.getAmenities().trim();
            amenitiesJson = s.isEmpty() ? "[]" : s;
        }

        LocalDateTime checkInTime = req.getCheckInTime();
        LocalDateTime checkOutTime = req.getCheckOutTime();
        String status = hasText(req.getStatus()) ? req.getStatus().trim() : null;
        BigDecimal minPrice = req.getMinPrice();

        int affected = accommodationRepository.patchUpdateScalars(
                id, name, address, latitude, longitude,
                thumbnail, description, amenitiesJson,
                checkInTime, checkOutTime, status, minPrice
        );
        if (affected == 0) throw new IllegalArgumentException("대상 없음: " + id);

        // 이미지/태그 덮어쓰기 ...
        return getById(id);
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    @Override
    @Transactional // 삭제 메서드가 업데이트,삭제 쿼리를 실행하므로 트랜잭션이 필요함
    public void delete(Long id) {
        // 연관 데이터 먼저 삭제
        accommodationImageRepository.deleteByAccommodation_AccommodationId(id);
        accommodationTagMapRepository.deleteByAccommodation_AccommodationId(id);
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
            dto.setThumbnail(thumbMap.get(dto.getAccommodationId()));
            return dto;
        }).toList();

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public Page<AccommodationListDTO> findByCategoryIdAndStatus(Long companyId, String status, Pageable pageable) {
        return accommodationRepository.findByCompany_CompanyIdAndStatus(companyId, status, pageable)
                .map(this::toListDTOWithThumbnail);
    }

    @Override
    public String getNameById(Long accommodationId) {
        if (accommodationId == null) return null;
        return accommodationRepository.findById(accommodationId).map(Accommodation::getName).orElse("알수없음");

    }

    @Override
    public AccommodationCreateRequestDTO getDetail(Long accId) {
        Accommodation acc = accommodationRepository.findById(accId)
                .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));

        // 기본 필드 채우기
        AccommodationCreateRequestDTO dto = AccommodationCreateRequestDTO.builder()
                .accommodationId(acc.getAccommodationId())
                .name(acc.getName())
                .address(acc.getAddress())
                .description(acc.getDescription())
                .build();

        // ▼ 최신 환불정책 1건만 조회
        cancellationPolicyRepository
                .findTop1ByAccommodation_AccommodationIdOrderByPolicyIdDesc(accId)
                .map(CancellationPolicy::getDetail)      // DB에 저장된 문자열(JSON/CSV/멀티라인 어떤 형식이든)
                .ifPresent(raw -> {
                    // 표준화: 리스트 + 멀티라인 둘 다 채움
                    List<String> items = AmenitiesParser.toList(raw);
                    dto.setCancellationPolicyItems(items);            // 화면에서 <li>로 돌릴 때 사용
                    dto.setCancellationPolicyDetail(String.join("\n", items)); // textarea/단락 표시용
                });

        return dto;
    }
    @Override
    public List<Long> getAccommodationIdsByAdminEmail(String email) {
        // JPQL(문자열 매칭) 우선
        List<Long> ids = accommodationRepository.findAccommodationIdsByAdminEmail(email);
        if (ids != null && !ids.isEmpty()) return ids;

        // 연관 매핑이 없거나 위 메서드가 없다면 네이티브로Fallback
        try {
            return accommodationRepository.findAccommodationIdsByAdminEmail(email);
        } catch (Exception ignore) {
            return List.of();
        }
    }
}

