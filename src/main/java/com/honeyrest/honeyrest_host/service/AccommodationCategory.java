package com.honeyrest.honeyrest_host.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dto.AccommodationCategoryDTO;
import com.honeyrest.honeyrest_host.repository.AccommodationCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationCategory {
    private final AccommodationCategoryRepository accommodationCategoryRepository;
    private final ModelMapper modelMapper;

    public AccommodationCategoryDTO getAccommodationCategory(Long id) {
        return modelMapper.map(accommodationCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("없어요")),AccommodationCategoryDTO.class);
    }
}
