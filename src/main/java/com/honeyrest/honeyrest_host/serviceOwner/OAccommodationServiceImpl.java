package com.honeyrest.honeyrest_host.serviceOwner;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.AccommodationImageDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.AccommodationImage;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repository.*;
import com.honeyrest.honeyrest_host.utilAdmin.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OAccommodationServiceImpl implements OAccommodationService {
    private final OAccommodationRepository accommodationRepository;
    private final OCompanyRepository companyRepository;
    private final ORegionRepository regionRepository;
    private final OAccommodationCategoryRepository accommodationCategoryRepository;
    private final OAccommodationImageRepository accommodationImageRepository;
    private final ObjectMapper objectMapper;
    private final FileUploadUtil fileUploadUtil;
    private final ORoomRepository roomRepository;


    //    private String parseAmenitiesToJson(String json) {
//        if (json == null || json.isBlank()) return "";
//        try {
//            // JSON을 Map으로 변환
//            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
//            // "key:value" 형식으로 변환 후 join
//            return map.entrySet().stream()
//                    .map(e -> e.getKey() + ":" + e.getValue())
//                    .collect(Collectors.joining(", "));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return json; // 실패하면 그냥 원본 JSON 반환
//        }
//    }
    //json문자열을 List로 변환
    private List<String> parseAmenitiesToList(String jsonInput) {
        if (jsonInput == null || jsonInput.isBlank()) return Collections.emptyList();

        try {
            // JSON 배열 문자열을 List<String>으로 역직렬화
            return objectMapper.readValue(jsonInput, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList(); // 실패 시 빈 리스트 반환
        }
    }
    //List를 문자열로 변환
    private String parseAmenitiesToString(String jsonInput) {
        List<String> list = parseAmenitiesToList(jsonInput);
        return String.join(", ", list);
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
                .amenities(parseAmenitiesToString(e.getAmenities()))
                .description(e.getDescription())
                .checkInTime(e.getCheckInTime())
                .checkOutTime(e.getCheckOutTime())
                .status(e.getStatus())
                .minPrice(e.getMinPrice())
                .rating(e.getRating())
                .build();
    }

    private String parseAmenitiesToJson(String input) {
        if (input == null || input.isBlank()) return "[]";
        try {
            List<String> amenitiesList = Arrays.stream(input.split("[,\\s]+"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            return objectMapper.writeValueAsString(amenitiesList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]"; // 실패 시 빈 배열 반환
        }
    }

    private Accommodation toEntity(AccommodationDTO d) {
        String amenitiesJson = "[]"; // 기본값

        try {
            if (d.getAmenities() != null && !d.getAmenities().isBlank()) {
                amenitiesJson = (parseAmenitiesToJson(d.getAmenities()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
    public void modifyAccommodation(AccommodationDTO dto) throws Exception {
        Accommodation acc = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));

        // 썸네일 이미지가 새로 업로드된 경우 처리
        MultipartFile newFile = dto.getFile();
        if (newFile != null && !newFile.isEmpty()) {
            String newThumbnailUrl = fileUploadUtil.upload(newFile, "accommodations");
            dto.setThumbnailUrl(newThumbnailUrl); // 새로운 썸네일로 덮어쓰기
        } else {
            // 새로 업로드한 파일이 없으면 기존 썸네일 유지
            dto.setThumbnailUrl(acc.getThumbnail());
        }

        // Entity로 변환 후 저장
        Accommodation updated = toEntity(dto);
        accommodationRepository.save(updated);
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

    @Override
    public PageResponseDTO<AccommodationDTO> getAccommodationsWithPageable(Long companyId, PageRequestDTO pageRequestDTO){
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("accommodationId").descending());

        Page<Accommodation> page;

        if (companyId != null && companyId >= 1) {
            page = accommodationRepository.findByCompany_CompanyId(companyId, pageable); // 쿼리 메서드 필요
        } else {
            page = accommodationRepository.findAll(pageable);
        }

        List<AccommodationDTO> list = page.getContent().stream().map(accommodation -> toDTO(accommodation))
                .filter(a-> a.getStatus().equalsIgnoreCase("ACTIVE"))
                .toList();

        long total = page.getTotalElements();

        PageResponseDTO<AccommodationDTO> responseDTO = PageResponseDTO.<AccommodationDTO>withAll()
                .dtoList(list)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(total)
                .build();

        return responseDTO;
    }

    @Override
    public PageResponseDTO<AccommodationDTO> getInActiveAccommodationsWithPageable(Long companyId, PageRequestDTO pageRequestDTO){
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("accommodationId").descending());

        Page<Accommodation> page;

        if (companyId != null && companyId > 0) {
            page = accommodationRepository.findByCompany_CompanyId(companyId, pageable); // 쿼리 메서드 필요
        } else {
            page = accommodationRepository.findAll(pageable);
        }

        List<AccommodationDTO> list = page.getContent().stream()
                .map(this::toDTO)
                .filter(a-> !a.getStatus().equalsIgnoreCase("active"))
                .toList();

        long total = page.getTotalElements();

        PageResponseDTO<AccommodationDTO> responseDTO = PageResponseDTO.<AccommodationDTO>withAll()
                .dtoList(list)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(total)
                .build();

        return responseDTO;
    }

    @Override
    public List<AccommodationImageDTO> getImagesByAccommodationId(Long id){
        return accommodationImageRepository.findByAccommodation_AccommodationId(id)
                .stream().map(this::toImageDTO).toList();

    }

    @Override
    public List<AccommodationImageDTO> getImagesByAccommodationIdOnlySub(Long id) {
        return accommodationImageRepository.findByAccommodation_AccommodationId(id)
                .stream().map(this::toImageDTO)
                .filter(si -> si.getImageType().equals("SUB"))
                .toList();
    }

    @Override
    public void updateSubImages(Long accommodationId, List<MultipartFile> images) throws Exception {
        if (images == null || images.isEmpty()) return;
        int sortOrder = 1; // MAIN 이미지 다음부터 시작



        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                String subImageUrl = fileUploadUtil.upload(image, "accommodations/" + accommodationId + "/images");

                AccommodationImageDTO imageDTO = AccommodationImageDTO.builder()
                        .imageUrl(subImageUrl)
                        .accommodationId(accommodationId)
                        .imageType("SUB")
                        .sortOrder(sortOrder++)
                        .build();

                registerAccommodationImage(imageDTO); // 또는 service.registerAccommodationImage
            }
        }
    }

    @Override
    public List<AccommodationDTO> searchByNameContaining(Long companyId, String keyword) {
        if (companyId == null || companyId == 0){
            return accommodationRepository.findByNameContainingIgnoreCase(keyword).stream().map(this::toDTO).toList();
        } else return accommodationRepository.findByCompany_CompanyIdAndNameContainingIgnoreCase(companyId, keyword)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public List<AccommodationDTO> searchByNameContaining(String keyword) {
        return accommodationRepository.findByNameContainingIgnoreCase(keyword).stream().map(this::toDTO).toList();
    }

    @Override
    public Long getAccommodationIdByRoomId(Long roomId) {
        Room room = roomRepository.findByRoomId(roomId);
        return room.getAccommodation().getAccommodationId();
    }

    @Override
    public AccommodationDTO getByName(String accommodationName){
        return toDTO(accommodationRepository.findByName(accommodationName));
    }
}
