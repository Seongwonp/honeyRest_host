package com.honeyrest.honeyrest_host.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.Company;
import com.honeyrest.honeyrest_host.repository.AccommodationRepository;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;
    private final AccommodationRepository accommodationRepository;

    private String parseBankInfoToJson(String input) {
        if (input == null || input.isBlank()) return "[]";
        try {
            List<String> amenitiesList = Arrays.stream(input.split("[,\\s]+"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            return objectMapper.writeValueAsString(amenitiesList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]"; // 실패 시 빈 배열 반환
        }
    }

//    private String parseBankInfoToJson(String json) {
//        if (json == null || json.isBlank()) return "";
//        try {
//            // JSON을 Map으로 변환
//            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
//            // "key:value" 형식으로 변환 후 join
//            return map.entrySet().stream()
//                    .map(e -> e.getKey() + ":" + e.getValue())
//                    .collect(Collectors.joining(", "));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return json; // 실패하면 그냥 원본 JSON 반환
//        }
//    }

    private CompanyDTO toDTO(Company company) {
        return CompanyDTO.builder()
                .name(company.getName())
                .companyId(company.getCompanyId())
                .address(company.getAddress())
                .phone(company.getPhone())
                .email(company.getEmail())
                .status(company.getStatus())
                .ownerName(company.getOwnerName())
                .bankInfo(parseBankInfoToString(company.getBankInfo()))
                .businessNumber(company.getBusinessNumber())
                .commissionRate(company.getCommissionRate())
                .build();
    }


    private List<String> parseBankInfoToList(String jsonInput) {
        if (jsonInput == null || jsonInput.isBlank()) return Collections.emptyList();

        try {
            // JSON 배열 문자열을 List<String>으로 역직렬화
            return objectMapper.readValue(jsonInput, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList(); // 실패 시 빈 리스트 반환
        }
    }
    private String parseBankInfoToString(String jsonInput) {
        List<String> list = parseBankInfoToList(jsonInput);
        return String.join(", ", list);
    }

    private Company toEntity(CompanyDTO dto) {
        String BankInfoJson = "[]"; // 기본값

        try {
            if (dto.getBankInfo() != null && !dto.getBankInfo().isBlank()) {
                BankInfoJson = objectMapper.writeValueAsString(parseBankInfoToJson(dto.getBankInfo()));
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

    public PageResponseDTO<CompanyDTO> getCompaniesWithPage(PageRequestDTO requestDTO){

        Pageable pageable = PageRequest.of(requestDTO.getPage() - 1, requestDTO.getSize(), Sort.by("companyId").descending());

        Page<Company> page = companyRepository.findAll(pageable);

        List<CompanyDTO> dtoList = page.getContent().stream()
                .map(this::toDTO) // Company -> CompanyDTO 변환 메서드 필요
                .toList();

        return PageResponseDTO.<CompanyDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(requestDTO)
                .totalCount(page.getTotalElements())
                .build();
    }

    public Long getCompanyIdByAccommodationId(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId);
        return accommodation.getCompany().getCompanyId();
    }

}
