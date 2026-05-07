package com.honeyrest.honeyrest_host.serviceAdmin;


import com.honeyrest.honeyrest_host.dtoAdmin.CompanyDTO;

import java.util.List;

public interface CompanyService {
    CompanyDTO create(CompanyDTO dto);
    CompanyDTO getById(Integer id);
    List<CompanyDTO> getAll();
    CompanyDTO update(Integer id, CompanyDTO dto);
    void delete(Integer id);

    CompanyDTO getByUserEmail(String email);

    Long getCompanyIdByUserEmail(String email);
    Long getCompanyIdByOfCurrentUser();
    Long getCompanyIdByAccommodationId(Long accommodationId);
}
