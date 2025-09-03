package com.honeyrest.honeyrest_host.serviceAdmin;


import com.honeyrest.honeyrest_host.dto.CompanyDTO;

import java.util.List;

public interface CompanyService {
    CompanyDTO create(CompanyDTO dto);
    CompanyDTO getById(Long id);
    List<CompanyDTO> getAll();
    CompanyDTO update(Long id, CompanyDTO dto);
    void delete(Long id);

    CompanyDTO getByUserEmail(String email);

    Long getCompanyIdByUserEmail(String email);
    Long getCompanyIdByOfCurrentUser();
    Long getCompanyIdByAccommodationId(Long accommodationId);
}
