package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCategoryDTO;

import com.honeyrest.honeyrest_host.entity.AccommodationCategory;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationCategoryServiceImpl implements AccommodationCategoryService {
    private final AccommodationCategoryRepository accommodationCategoryRepository;

    private AccommodationCategoryDTO toDTO(AccommodationCategory e) {
        return AccommodationCategoryDTO.builder()
                .categoryId(e.getCategoryId())
                .name(e.getName())
                .iconUrl(e.getName())
                .sortOrder(e.getSortOrder())
                .build();
    }
    @Override
    public List<AccommodationCategoryDTO> list() {
        return accommodationCategoryRepository.findAllByOrderBySortOrderAscCategoryIdAsc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public AccommodationCategoryDTO get(Long id) {
        var e = accommodationCategoryRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("카테고리가 존재하지 않습니다."));
        return toDTO(e);
    }
}
