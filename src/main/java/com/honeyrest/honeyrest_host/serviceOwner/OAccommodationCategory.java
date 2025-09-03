package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationCategoryDTO;
import com.honeyrest.honeyrest_host.repository.OAccommodationCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OAccommodationCategory {
    private final OAccommodationCategoryRepository accommodationCategoryRepository;
    private final ModelMapper modelMapper;

    public List<AccommodationCategoryDTO> getAllAccommodationCategory() {
        return accommodationCategoryRepository.findAll()
                .stream().map(c->modelMapper.map(c, AccommodationCategoryDTO.class)).toList();

    }
}
