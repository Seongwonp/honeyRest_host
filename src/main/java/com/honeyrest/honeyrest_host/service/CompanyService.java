package com.honeyrest.honeyrest_host.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.entity.Company;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;

    private String parseCompanyJson(String json) {
        if (json == null || json.isBlank()) return "";
        try {
            // JSON을 Map으로 변환
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            // "key:value" 형식으로 변환 후 join
            return map.entrySet().stream()
                    .map(e -> e.getKey() + ":" + e.getValue())
                    .collect(Collectors.joining(", "));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return json; // 실패하면 그냥 원본 JSON 반환
        }
    }

    private CompanyDTO toDTO(Company company) {
        return CompanyDTO.builder()
                .name(company.getName())
                .companyId(company.getCompanyId())
                .address(company.getAddress())
                .phone(company.getPhone())
                .email(company.getEmail())
                .status(company.getStatus())
                .ownerName(company.getOwnerName())
                .bankInfo(parseCompanyJson(company.getBankInfo()))
                .businessNumber(company.getBusinessNumber())
                .commissionRate(company.getCommissionRate())
                .build();
    }


    private Map<String, Object> parseBankInfoString(String str) {
        Map<String, Object> map = new HashMap<>();
        String[] entries = str.split(",");
        for (String entry : entries) {
            String[] kv = entry.trim().split(":");
            if (kv.length == 2) {
                String key = kv[0].trim();
                String valueStr = kv[1].trim();
                Object value = "true".equalsIgnoreCase(valueStr) ? true :
                        "false".equalsIgnoreCase(valueStr) ? false : valueStr;
                map.put(key, value);
            }
        }
        return map;
    }

    private Company toEntity(CompanyDTO dto) {
        String BankInfoJson = "[]"; // 기본값

        try {
            if (dto.getBankInfo() != null && !dto.getBankInfo().isBlank()) {
                BankInfoJson = objectMapper.writeValueAsString(parseBankInfoString(dto.getBankInfo()));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // 변환 실패 시 로그 출력
        }
        return Company.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .companyId(dto.getCompanyId())
                .status(dto.getStatus())
                .bankInfo(BankInfoJson)
                .businessNumber(dto.getBusinessNumber())
                .commissionRate(dto.getCommissionRate())
                .ownerName(dto.getOwnerName())
                .build();
    }

    public void registerCompany(CompanyDTO dto) {
        companyRepository.save(toEntity(dto));
    }

    public void removeCompany(Long id) {
        companyRepository.deleteById(id);
    }

    public void modifyCompany(CompanyDTO dto) {
        companyRepository.save(toEntity(dto));
    }

    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public CompanyDTO getCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("숙소가 존재하지 않습니다."));
        return toDTO(company);
    }
}
