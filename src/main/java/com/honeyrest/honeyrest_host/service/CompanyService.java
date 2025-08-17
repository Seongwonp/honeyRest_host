package com.honeyrest.honeyrest_host.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.entity.Company;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    public void registerCompany(CompanyDTO dto) {
        Company company = modelMapper.map(dto, Company.class);
        companyRepository.save(company);
    }

    public void removeCompany(Long id) {
        companyRepository.deleteById(id);
    }

    public void modifyCompany(CompanyDTO dto) {
        Company company = modelMapper.map(dto, Company.class);
    }

    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CompanyDTO.class))
                .toList();
    }

    public CompanyDTO getCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));
        return modelMapper.map(company, CompanyDTO.class);
    }
}
