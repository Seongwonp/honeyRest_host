package com.honeyrest.honeyrest_host.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.AccommodationImageDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.AccommodationImage;
import com.honeyrest.honeyrest_host.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final CompanyRepository companyRepository;
    private final RegionRepository regionRepository;
    private final AccommodationCategoryRepository accommodationCategoryRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final ObjectMapper objectMapper;


    private String parseAmenitiesJson(String json) {
        if (json == null || json.isBlank()) return "";
        try {
            // JSON을 Map으로 변환
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            // "key:value" 형식으로 변환 후 join
            return map.entrySet().stream()
                    .map(e -> e.getKey() + ":" + e.getValue())
                    .collect(Collectors.joining(", "));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return json; // 실패하면 그냥 원본 JSON 반환
        }
    }
    // ===== Mapper =====
    private AccommodationDTO toDTO(Accommodation e) {
        return AccommodationDTO.builder()
                .AccommodationId(e.getAccommodationId())
                .companyId(e.getCompany().getCompanyId())
                .categoryId(e.getCategory().getCategoryId())
                .mainRegionId(e.getMainRegion().getRegionId())
                .subRegionId(e.getSubRegion().getRegionId())
                .name(e.getName())
                .address(e.getAddress())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .thumbnailUrl(e.getThumbnail())
                .amenities(parseAmenitiesJson(e.getAmenities()))
                .description(e.getDescription())
                .checkInTime(e.getCheckInTime())
                .checkOutTime(e.getCheckOutTime())
                .status(e.getStatus())
                .minPrice(e.getMinPrice())
                .rating(e.getRating())
                .build();
    }

    /**
     * "wifi:true, tv:true" -> Map<String,Object> 변환
     */
    private Map<String, Object> parseAmenitiesString(String str) {
        Map<String, Object> map = new HashMap<>();
        String[] entries = str.split(",");
        for (String entry : entries) {
            String[] kv = entry.trim().split(":");
            if (kv.length == 2) {
                String key = kv[0].trim();
                String valueStr = kv[1].trim();
                Object value = "true".equalsIgnoreCase(valueStr) ? true :
                        "false".equalsIgnoreCase(valueStr) ? false : valueStr;
                map.put(key, value);
            }
        }
        return map;
    }

    private Accommodation toEntity(AccommodationDTO d) {
        String amenitiesJson = "[]"; // 기본값

        try {
            if (d.getAmenities() != null && !d.getAmenities().isBlank()) {
                amenitiesJson = objectMapper.writeValueAsString(parseAmenitiesString(d.getAmenities()));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // 변환 실패 시 로그 출력
        }

        return Accommodation.builder()
                .accommodationId(d.getAccommodationId())
                .company(companyRepository.getReferenceById(d.getCompanyId()))
                .category(accommodationCategoryRepository.getReferenceById(d.getCategoryId()))
                .mainRegion(regionRepository.getReferenceById(d.getMainRegionId()))
                .subRegion(regionRepository.getReferenceById(d.getSubRegionId()))
                .name(d.getName())
                .address(d.getAddress())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .amenities(amenitiesJson)
                .thumbnail(d.getThumbnailUrl())
                .description(d.getDescription())
                .checkInTime(d.getCheckInTime())
                .checkOutTime(d.getCheckOutTime())
                .status(d.getStatus() == null ? "ACTIVE" : d.getStatus())
                .minPrice(d.getMinPrice())
                .rating(d.getRating())
                .build();
    }



    private AccommodationImageDTO toImageDTO(AccommodationImage e) {
        return AccommodationImageDTO.builder()
                .accommodationId(e.getAccommodation().getAccommodationId())
                .imageUrl(e.getImageUrl())
                .sortOrder(e.getSortOrder())
                .imageId(e.getImageId())
                .imageType(e.getImageType())
                .build();
    }

    private AccommodationImage toImageEntity(AccommodationImageDTO d) throws JsonProcessingException {
        return AccommodationImage.builder()
                .imageId(d.getImageId())
                .imageUrl(d.getImageUrl())
                .sortOrder(d.getSortOrder())
                .imageType(d.getImageType())
                .accommodation(accommodationRepository.getReferenceById(d.getAccommodationId()))
                .build();
    }
    @Override
    public List<AccommodationDTO> getAllAccommodations() {
        return accommodationRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public List<AccommodationDTO> getAccommodationsByCompanyId(Long companyId) {
        return accommodationRepository.findByCompany_CompanyId(companyId).stream().map(this::toDTO).toList();
    }



    @Override
    public AccommodationDTO getByAccommodationId(Long accommodationId) {
        Accommodation acc = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));

        return toDTO(acc);
    }

    @Override
    public Long registerAccommodation(AccommodationDTO dto) throws JsonProcessingException {
        Accommodation acc = toEntity(dto);


        return accommodationRepository.save(acc).getAccommodationId();
    }

    @Override
    public void modifyAccommodation(AccommodationDTO dto) throws JsonProcessingException {
        Accommodation acc = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));
        accommodationRepository.save(toEntity(dto));
    }

    @Override
    public void removeAccommodation(Long id) {
        Accommodation acc = accommodationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));

        accommodationRepository.deleteById(id);
    }

    @Override
    public void registerAccommodationImage(AccommodationImageDTO dto) throws JsonProcessingException {
        accommodationImageRepository.save(toImageEntity(dto));
    }
}
