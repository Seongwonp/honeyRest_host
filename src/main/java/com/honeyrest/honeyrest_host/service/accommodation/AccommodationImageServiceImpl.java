package com.honeyrest.honeyrest_host.service.accommodation;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationImageDTO;
import com.honeyrest.honeyrest_host.entity.AccommodationImage;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationImageRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class AccommodationImageServiceImpl implements AccommodationImageService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final FileUploadUtil fileUploadUtil;

    private AccommodationImageDTO toDTO(AccommodationImage e) {
        return AccommodationImageDTO.builder()
                .imageId(e.getImageId())
                .imageUrl(e.getImageUrl())
                .imageType(e.getImageType())
                .sortOrder(e.getSortOrder())
                .accommodationId(e.getAccommodation().getAccommodationId())
                .build();
    }


    @Override
    // 새로 추가: 파일이 있으면 업로드, 없으면 imageUrl 사용해서 저장
    public AccommodationImageDTO saveOrUpload(Long accommodationId, AccommodationImageDTO dto) {
        var accRef = accommodationRepository.getReferenceById(accommodationId);

        String imageUrl = dto.getImageUrl();
        // 파일이 있으면 업로드가 우선
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
        Integer sort = dto.getSortOrder() == null ? 0 : dto.getSortOrder();

        var entity = AccommodationImage.builder()
                .accommodation(accRef)
                .imageUrl(imageUrl)
                .imageType(type)
                .sortOrder(sort)
                .build();

        return toDTO(accommodationImageRepository.save(entity));
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccommodationImageDTO> getImages(Long accommodationId) {
        return accommodationImageRepository
                .findByAccommodation_AccommodationIdOrderBySortOrderAsc(accommodationId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccommodationImageDTO getMainImage(Long accommodationId, String imageType) {
        String type = (imageType == null || imageType.isBlank()) ? "MAIN" : imageType;
        return accommodationImageRepository
                .findFirstByAccommodation_AccommodationIdAndImageTypeOrderBySortOrderAscImageIdAsc(accommodationId, type)
                .map(this::toDTO)
                .orElse(null);
    }

    @Override
    public void delete(Long imageId) {
        accommodationImageRepository.deleteById(imageId);

    }

    @Override
    public void updateSort(Long ImageId, Integer sortOrder) {
        AccommodationImage img = accommodationImageRepository.findById(ImageId)
                .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다: " + ImageId));

        AccommodationImage updated = AccommodationImage.builder()
                .imageId(img.getImageId())
                .accommodation(img.getAccommodation())
                .imageUrl(img.getImageUrl())
                .imageType(img.getImageType())
                .sortOrder(sortOrder)
                .build();

        accommodationImageRepository.save(updated);
    }


    @Override
    public AccommodationImageDTO upsertMainThumbnail(Long accommodationId, AccommodationImageDTO dto) {
        var accRef = accommodationRepository.getReferenceById(accommodationId);

        String imageUrl = dto.getImageUrl();
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            try {
                String folder = "accommodations/" + accommodationId + "/thumbnail";
                imageUrl = fileUploadUtil.upload(dto.getFile(), folder);
            } catch (Exception e) {
                throw new IllegalStateException("썸네일 업로드 실패", e);
            }
        }else if (dto.getImageType() == null || dto.getImageType().isBlank()) {
            imageUrl = dto.getImageUrl();
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("메인 썸네일에는 파일 또는 imageUrl이 필요합니다.");
        }
        final String finalImageUrl = imageUrl;

        var saved =  accommodationImageRepository
                .findFirstByAccommodation_AccommodationIdAndImageTypeOrderBySortOrderAscImageIdAsc(accommodationId, "MAIN")
                .map(existing -> AccommodationImage.builder()
                            .imageId(existing.getImageId())
                            .accommodation(accRef)
                            .imageType("MAIN")
                            .imageUrl(finalImageUrl)
                            .sortOrder(existing.getSortOrder())
                            .build())
                .orElseGet(() -> AccommodationImage.builder()
                        .accommodation(accRef)
                        .imageType("MAIN")
                        .imageUrl(finalImageUrl)
                        .sortOrder(0)
                        .build());
                    return toDTO(accommodationImageRepository.save(saved));
                }


    @Override
    public List<AccommodationImageDTO> getByAccommodation_AccommodationId(Long accommodationId,String imageType) {
        return accommodationImageRepository
                .findFirstByAccommodation_AccommodationIdAndImageTypeOrderBySortOrderAscImageIdAsc(accommodationId, "MAIN")
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}