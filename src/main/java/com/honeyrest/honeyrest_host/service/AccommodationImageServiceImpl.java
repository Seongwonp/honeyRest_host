package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationImageDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.AccommodationImage;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationImageRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public AccommodationImageDTO upload(Long accommodationId, AccommodationImageDTO accommodationImageDTO) {
        Accommodation accRef = accommodationRepository.getReferenceById(accommodationId);

        MultipartFile file = accommodationImageDTO.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 없습니다");

        }
        String folder = "accommodations/" + accommodationId + "/images";
        String imageUrl;
        try {
            imageUrl = fileUploadUtil.upload(file, folder);
        } catch (Exception e) {
            throw new IllegalArgumentException("이미지 업로드 실패", e);
        }
        String type = accommodationImageDTO.getImageType() != null ? accommodationImageDTO.getImageType() : "MAIN";

        AccommodationImage entity = AccommodationImage.builder()
                .accommodation(accRef)
                .imageUrl(imageUrl)
                .imageType(type)
                .sortOrder(accommodationImageDTO.getSortOrder())
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
    public AccommodationImageDTO getMainImage(Long accommodationId,String imageType) {
        return accommodationImageRepository
                .findFirstByAccommodation_AccommodationIdAndImageTypeOrderBySortOrderAscImageIdAsc(accommodationId, "MAIN")
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
        Accommodation accRef = accommodationRepository.getReferenceById(accommodationId);

        String imageUrl = null;
        // 업로드 실행
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            try {
                String folder = "accommodations/" + accommodationId + "/thumbnail";
                imageUrl = fileUploadUtil.upload(dto.getFile(), folder);
            } catch (Exception e) {
                throw new IllegalStateException("썸네일 업로드 실패", e);
            }
            if (imageUrl == null && dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
                throw new IllegalArgumentException("메인 썸네일 생성에는 파일 또는 imageUrl이 필요합니다.");
            }

            String finalImageUrl = imageUrl;
            String finalImageUrl1 = imageUrl;
            return accommodationImageRepository
                    .findFirstByAccommodation_AccommodationIdAndImageTypeOrderBySortOrderAscImageIdAsc(accommodationId, "MAIN")
                    .map(existing -> {
                        AccommodationImage replaced = AccommodationImage.builder()
                                .imageId(existing.getImageId())
                                .accommodation(accRef)
                                .imageType("MAIN")
                                .imageUrl(finalImageUrl != null ? finalImageUrl : existing.getImageUrl())
                                .sortOrder(existing.getSortOrder())
                                .build();
                        return toDTO(accommodationImageRepository.save(replaced));
                    })
                    .orElseGet(() -> {
                        AccommodationImage created = AccommodationImage.builder()
                                .accommodation(accRef)
                                .imageType("MAIN")
                                .imageUrl(finalImageUrl1)
                                .sortOrder(0)
                                .build();
                        return toDTO(accommodationImageRepository.save(created));
                    });

        }
        return dto;
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