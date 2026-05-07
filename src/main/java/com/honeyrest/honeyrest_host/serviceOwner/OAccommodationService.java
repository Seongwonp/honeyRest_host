package com.honeyrest.honeyrest_host.serviceOwner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.AccommodationImageDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OAccommodationService {
    List<AccommodationDTO> getAllAccommodations();

    List<AccommodationDTO> getAccommodationsByCompanyId(Integer companyId);

    AccommodationDTO getByAccommodationId(Long accommodationId);

    Long registerAccommodation(AccommodationDTO accommodationDTO) throws JsonProcessingException;

    void modifyAccommodation(AccommodationDTO dto) throws Exception;

    void removeAccommodation(Long id);

    void registerAccommodationImage(AccommodationImageDTO dto) throws JsonProcessingException;

    PageResponseDTO<AccommodationDTO> getAccommodationsWithPageable(Integer companyId, PageRequestDTO pageRequestDTO);

    PageResponseDTO<AccommodationDTO> getInActiveAccommodationsWithPageable(Integer companyId, PageRequestDTO pageRequestDTO);

    List<AccommodationImageDTO> getImagesByAccommodationId(Long id);

    List<AccommodationImageDTO> getImagesByAccommodationIdOnlySub(Long id);

    void updateSubImages(Long accommodationId, List<MultipartFile> images) throws Exception;

    List<AccommodationDTO> searchByNameContaining(Integer companyId, String keyword);

    List<AccommodationDTO> searchByNameContaining(String keyword);


    Long getAccommodationIdByRoomId(Long roomId);

    AccommodationDTO getByName(String accommodationName);
}
