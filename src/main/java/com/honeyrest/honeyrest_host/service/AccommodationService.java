package com.honeyrest.honeyrest_host.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.honeyrest.honeyrest_host.dto.AccommodationDTO;

import java.util.List;

public interface AccommodationService {
    List<AccommodationDTO> getAllAccommodations();

    AccommodationDTO getByAccommodationId(Long accommodationId);

    Long registerAccommodation(AccommodationDTO accommodationDTO) throws JsonProcessingException;

    void modifyAccommodation(AccommodationDTO dto) throws JsonProcessingException;

    void removeAccommodation(Long id);
}
