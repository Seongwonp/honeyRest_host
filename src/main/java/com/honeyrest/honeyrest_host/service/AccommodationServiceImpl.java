package com.honeyrest.honeyrest_host.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.honeyrest.honeyrest_host.dto.AccommodationDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.repository.AccommodationRepository;
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
    private final ModelMapper modelMapper;

    @Override
    public List<AccommodationDTO> getAllAccommodations() {
        return accommodationRepository.findAll().stream().map(accommodation ->
                modelMapper.map(accommodation , AccommodationDTO.class)).toList();
    }



    @Override
    public AccommodationDTO getByAccommodationId(Long accommodationId) {
        Accommodation acc = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));

        return modelMapper.map(acc , AccommodationDTO.class);
    }

    @Override
    public Long registerAccommodation(AccommodationDTO dto) {
        Accommodation acc = modelMapper.map(dto, Accommodation.class);

        accommodationRepository.save(acc);
        return acc.getAccommodationId();
    }

    @Override
    public void modifyAccommodation(AccommodationDTO dto) {
        Accommodation acc = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));
        accommodationRepository.save(modelMapper.map(dto, Accommodation.class));
    }

    @Override
    public void removeAccommodation(Long id) {
        Accommodation acc = accommodationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));

        // Rooms/Images는 orphanRemoval=true 이므로 자동 삭제 (연관관계 매핑 기준)
        accommodationRepository.delete(acc);
    }
}
