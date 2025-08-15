package com.honeyrest.honeyrest_host.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dto.AccommodationDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.repository.AccommodationCategoryRepository;
import com.honeyrest.honeyrest_host.repository.AccommodationRepository;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import com.honeyrest.honeyrest_host.repository.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final CompanyRepository companyRepository;
    private final RegionRepository regionRepository;
    private final AccommodationCategoryRepository accommodationCategoryRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private JsonNode stringToJsonNode(String json) {
        try {
            if (json == null || json.isBlank()) return objectMapper.readTree("[]");
            return objectMapper.readTree(json);
        } catch (Exception e) {
            try { return objectMapper.readTree("[]"); } catch (Exception ignored) { return null; }
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

    // ===== Mapper =====
    private AccommodationDTO toDTO(Accommodation e) {
        return AccommodationDTO.builder()
                .AccommodationId(e.getAccommodationId())
                .companyId(e.getCompany() != null ? e.getCompany().getCompanyId() : null)
                .categoryId(e.getCategory() != null ? e.getCategory().getCategoryId() : null)
                .mainRegionId(e.getMainRegion() != null ? e.getMainRegion().getRegionId() : null)
                .subRegionId(e.getSubRegion() != null ? e.getSubRegion().getRegionId() : null)
                .name(e.getName())
                .address(e.getAddress())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .thumbnailUrl(e.getThumbnail())
                .amenities(stringToJsonNode(e.getAmenities()))
                .description(e.getDescription())
                .checkInTime(e.getCheckInTime())
                .checkOutTime(e.getCheckOutTime())
                .status(e.getStatus())
                .build();
    }

    private Accommodation toEntity(AccommodationDTO d) {
        // 필수값 검증: 엔티티에서 nullable=false 인 필드들
        if (d.getCompanyId() == null || d.getCategoryId() == null ||
            d.getMainRegionId() == null || d.getSubRegionId() == null ||
            d.getName() == null || d.getAddress() == null) {
            throw new IllegalArgumentException("companyId, categoryId, mainRegionId, subRegionId, name, address 는 필수입니다.");
        }

        return Accommodation.builder()
                .company(companyRepository.getReferenceById(d.getCompanyId()))
                .category(accommodationCategoryRepository.getReferenceById(d.getCategoryId()))
                .mainRegion(regionRepository.getReferenceById(d.getMainRegionId()))
                .subRegion(regionRepository.getReferenceById(d.getSubRegionId()))
                .name(d.getName())
                .address(d.getAddress())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .thumbnail(d.getThumbnailUrl())
                .amenities(jsonNodeToString(d.getAmenities()))
                .description(d.getDescription())
                .checkInTime(d.getCheckInTime())
                .checkOutTime(d.getCheckOutTime())
                .status(d.getStatus() == null ? "ACTIVE" : d.getStatus())
                .build();
    }

    @Override
    public List<AccommodationDTO> getAllAccommodations() {
        return accommodationRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }



    @Override
    public AccommodationDTO getByAccommodationId(Long accommodationId) {
        Accommodation acc = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new EntityNotFoundException("숙소가 존재하지 않습니다."));

        return toDTO(acc);
    }

    @Override
    public AccommodationDTO registerAccommodation(AccommodationDTO dto) {
        Accommodation acc = toEntity(dto);
        Accommodation saved = accommodationRepository.save(acc); // ← 저장된 엔티티 반환 사용
        return toDTO(saved);
    }

    @Override
    public void modifyAccommodation(AccommodationDTO dto) {
        Accommodation current = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new EntityNotFoundException("숙소가 존재하지 않습니다."));

        Accommodation updated = Accommodation.builder()
                .accommodationId(current.getAccommodationId())
                // 연관: DTO에 값이 있으면 참조로 교체, 아니면 기존 유지
                .company(dto.getCompanyId() != null ?
                        companyRepository.getReferenceById(dto.getCompanyId()) : current.getCompany())
                .category(dto.getCategoryId() != null ?
                        accommodationCategoryRepository.getReferenceById(dto.getCategoryId()) : current.getCategory())
                .mainRegion(dto.getMainRegionId() != null ?
                        regionRepository.getReferenceById(dto.getMainRegionId()) : current.getMainRegion())
                .subRegion(dto.getSubRegionId() != null ?
                        regionRepository.getReferenceById(dto.getSubRegionId()) : current.getSubRegion())
                // 값 타입/단순 필드: DTO 값이 있으면 덮어쓰고, 없으면 기존 유지
                .name(dto.getName() != null ? dto.getName() : current.getName())
                .address(dto.getAddress() != null ? dto.getAddress() : current.getAddress())
                .latitude(dto.getLatitude() != null ? dto.getLatitude() : current.getLatitude())
                .longitude(dto.getLongitude() != null ? dto.getLongitude() : current.getLongitude())
                .thumbnail(dto.getThumbnailUrl() != null ? dto.getThumbnailUrl() : current.getThumbnail())
                .amenities(dto.getAmenities() != null ? jsonNodeToString(dto.getAmenities()) : current.getAmenities())
                .description(dto.getDescription() != null ? dto.getDescription() : current.getDescription())
                .checkInTime(dto.getCheckInTime() != null ? dto.getCheckInTime() : current.getCheckInTime())
                .checkOutTime(dto.getCheckOutTime() != null ? dto.getCheckOutTime() : current.getCheckOutTime())
                .status(dto.getStatus() != null ? dto.getStatus() : current.getStatus())
                .build();

        accommodationRepository.save(updated);
    }

    @Override
    public void removeAccommodation(Long id) {
        Accommodation acc = accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("숙소가 존재하지 않습니다."));

        // Rooms/Images는 orphanRemoval=true 이므로 자동 삭제 (연관관계 매핑 기준)
        accommodationRepository.deleteById(id);
    }
}