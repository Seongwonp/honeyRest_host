package com.honeyrest.honeyrest_host.service.accommodation;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationImageDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.AccommodationImage;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationImageRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class AccommodationImageServiceImpl implements AccommodationImageService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final FileUploadUtil fileUploadUtil;

    private AccommodationImageDTO toDTO(AccommodationImage e) {
        return AccommodationImageDTO.builder().
                imageId(e.getImageId()).imageUrl(e.getImageUrl())
                .imageType(e.getImageType())
                .sortOrder(e.getSortOrder())
                .imageUrl(e.getImageUrl())
                .accommodationId(e.getAccommodation().getAccommodationId())
                .build();
    }


    @Override
    // 새로 추가: 파일이 있으면 업로드, 없으면 imageUrl 사용해서 저장
    public AccommodationImageDTO saveOrUpload(Long accommodationId, AccommodationImageDTO dto) {
        Accommodation accRef = accommodationRepository.getReferenceById(accommodationId);

        String imageUrl = dto.getImageUrl();
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            try {
                String folder = "accommodations/" + accommodationId + "/images";
                imageUrl = fileUploadUtil.upload(dto.getFile(), folder);
            } catch (Exception e) {
                throw new IllegalArgumentException("이미지 업로드 실패", e);
            }
        }

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("이미지 파일 또는 imageUrl 중 하나는 필요합니다.");
        }

        String type = (dto.getImageType() == null || dto.getImageType().isBlank()) ? "SUB" : dto.getImageType();
        if ("MAIN".equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("MAIN 이미지는 upsertMainThumbnail()을 사용하세요.");
        }

        Integer sort = (dto.getSortOrder() == null) ? 1 : dto.getSortOrder(); // MAIN=0, SUB는 1부터

        AccommodationImage entity = AccommodationImage.builder()
                .accommodation(accRef)
                .imageUrl(imageUrl)
                .imageType(type)
                .sortOrder(sort)
                .build();

        AccommodationImage saved = accommodationImageRepository.save(entity);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccommodationImageDTO> getImages(Long accommodationId) {
        return accommodationImageRepository.findByAccommodation_AccommodationIdOrderBySortOrderAsc(accommodationId).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccommodationImageDTO getMainImage(Long accommodationId, String imageType) {
        String type = (imageType == null || imageType.isBlank()) ? "MAIN" : imageType;
        return accommodationImageRepository.findFirstByAccommodation_AccommodationIdAndImageTypeOrderBySortOrderAscImageIdAsc(accommodationId, type).map(this::toDTO).orElse(null);
    }

    @Override
    public void delete(Long imageId) {
        AccommodationImage image = accommodationImageRepository.findById(imageId).orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));
        // 폴더 안전장치
        String folder = "accommodations/" + image.getAccommodation().getAccommodationId() + "/images";
        fileUploadUtil.delete(folder, image.getImageUrl());
        accommodationImageRepository.delete(image);

    }

    @Override
    public void deleteSubImages(List<Long> imageIds) {
        if(imageIds == null || imageIds.isEmpty()) {
            return;
        }
        accommodationImageRepository.deleteAllById(imageIds);
    }

    @Override
    public void updateSort(Long ImageId, Integer sortOrder) {
        AccommodationImage img = accommodationImageRepository.findById(ImageId).orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다: " + ImageId));

        AccommodationImage updated = AccommodationImage.builder().imageId(img.getImageId()).accommodation(img.getAccommodation()).imageUrl(img.getImageUrl()).imageType(img.getImageType()).sortOrder(sortOrder).build();

        accommodationImageRepository.save(updated);
    }


    @Override
    @Transactional
    public AccommodationImageDTO upsertMainThumbnail(Long accommodationId, AccommodationImageDTO dto) {
        Accommodation accRef = accommodationRepository.getReferenceById(accommodationId);

        // 1) 업로드 or URL
        String imageUrl = dto.getImageUrl();
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            String folder = "accommodations/" + accommodationId + "/thumbnail";
            try {
                imageUrl = fileUploadUtil.upload(dto.getFile(), folder);
            } catch (Exception e) {
                throw new IllegalStateException("썸네일 업로드 실패", e);
            }
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("메인 썸네일에는 파일 또는 imageUrl이 필요합니다.");
        }

        // 2) 기존 MAIN 전부 제거해서 '항상 1장' 보장
        accommodationImageRepository.deleteByAccommodation_AccommodationIdAndImageType(accommodationId, "MAIN");

        // 3) 새 MAIN 저장
        AccommodationImage entity = AccommodationImage.builder()
                .accommodation(accRef)
                .imageType("MAIN")
                .sortOrder(0)
                .imageUrl(imageUrl)
                .build();

        AccommodationImage saved = accommodationImageRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public List<AccommodationImageDTO> getByAccommodation_AccommodationId(Long accommodationId, String imageType) {
        String type = (imageType == null || imageType.isBlank()) ? "SUB" : imageType.trim().toUpperCase();
        return accommodationImageRepository
                .findByAccommodation_AccommodationIdAndImageTypeOrderBySortOrderAscImageIdAsc(accommodationId, type)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateThumbnailUrl(Long accommodationId, String thumbnailUrl) {
        Accommodation acc = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다: " + accommodationId));

        Accommodation updated = Accommodation.builder()
                .accommodationId(acc.getAccommodationId())
                .company(acc.getCompany())
                .category(acc.getCategory())
                .mainRegion(acc.getMainRegion())
                .subRegion(acc.getSubRegion())
                .name(acc.getName())
                .address(acc.getAddress())
                .thumbnail(thumbnailUrl)
                .description(acc.getDescription())
                .status(acc.getStatus())
                .checkInTime(acc.getCheckInTime())
                .checkOutTime(acc.getCheckOutTime())
                .rating(acc.getRating())
                .minPrice(acc.getMinPrice())
                .build();

        accommodationRepository.save(updated);
    }
}