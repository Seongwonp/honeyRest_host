package com.honeyrest.honeyrest_host.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.AccommodationImageDTO;

import java.util.List;

public interface AccommodationService {
    List<AccommodationDTO> getAllAccommodations();

    List<AccommodationDTO> getAccommodationsByCompanyId(Long companyId);

    AccommodationDTO getByAccommodationId(Long accommodationId);

    Long registerAccommodation(AccommodationDTO accommodationDTO) throws JsonProcessingException;

    void modifyAccommodation(AccommodationDTO dto) throws JsonProcessingException;

    void removeAccommodation(Long id);

    void registerAccommodationImage(AccommodationImageDTO dto) throws JsonProcessingException;
}
