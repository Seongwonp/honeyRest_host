package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.AccommodationDTO;

import java.util.List;

public interface AccommodationService {
    List<AccommodationDTO> getAllAccommodations();

    AccommodationDTO getByAccommodationId(Long accommodationId);

    Long registerAccommodation(AccommodationDTO accommodationDTO);

    void modifyAccommodation(AccommodationDTO dto);

    void removeAccommodation(Long id);
}
