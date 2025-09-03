package com.honeyrest.honeyrest_host.serviceOwner;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.Company;
import com.honeyrest.honeyrest_host.repository.OAccommodationRepository;
import com.honeyrest.honeyrest_host.repository.OCompanyRepository;
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
public class OCompanyService {
    private final OCompanyRepository companyRepository;
    private final ObjectMapper objectMapper;
    private final OAccommodationRepository accommodationRepository;

    // OCompanyService.java (필드로 두면 재사용 가능)
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

    private List<String> parseBankInfoToList(String json) {
        if (json == null || json.isBlank()) return List.of();

        try {
            JsonNode node = mapper.readTree(json);

            if (node.isArray()) {
                List<String> out = new ArrayList<>();
                for (JsonNode n : node) out.add(n.asText());
                return out;
            }

            if (node.isObject()) {
                // 객체로 저장되어 온 경우 값들만 뽑아 문자열 리스트로 변환
                List<String> out = new ArrayList<>();
                node.fields().forEachRemaining(e -> {
                    JsonNode v = e.getValue();
                    out.add(v.isTextual() ? v.asText() : v.toString());
                    // 필요하면 키 포함 형식으로: out.add(e.getKey() + ": " + (v.isTextual()? v.asText() : v.toString()));
                });
                return out;
            }

            if (node.isTextual()) {
                return List.of(node.asText());
            }

            // 그 외 타입은 문자열화
            return List.of(node.toString());

        } catch (Exception ex) {
            // 로그 남기고 빈 리스트 반환 (혹은 예외 다시 던지기)
            // log.warn("Invalid bankInfo JSON: {}", json, ex);
            return List.of();
        }
    }

    private String parseBankInfoToString(String json) {
        return String.join(", ", parseBankInfoToList(json));
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
                .bankInfo(parseBankInfoToString(company.getBankInfo()))
                .businessNumber(company.getBusinessNumber())
                .commissionRate(company.getCommissionRate())
                .createdAt(company.getCreatedAt())
                .build();
    }

    private Company toEntity(CompanyDTO dto) {
        String BankInfoJson = "[]"; // 기본값

        try {
            if (dto.getBankInfo() != null && !dto.getBankInfo().isBlank()) {
                BankInfoJson = parseBankInfoToString(dto.getBankInfo());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                .filter(c -> "active".equalsIgnoreCase(c.getStatus()))
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
                .filter(c -> "active".equalsIgnoreCase(c.getStatus()))
                .toList();

        return PageResponseDTO.<CompanyDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(requestDTO)
                .totalCount(page.getTotalElements())
                .build();
    }


    public PageResponseDTO<CompanyDTO> getInActiveCompaniesWithPage(PageRequestDTO requestDTO){

        Pageable pageable = PageRequest.of(requestDTO.getPage() - 1, requestDTO.getSize(), Sort.by("companyId").descending());

        Page<Company> page = companyRepository.findAll(pageable);

        List<CompanyDTO> dtoList = page.getContent().stream()
                .map(this::toDTO) // Company -> CompanyDTO 변환 메서드 필요
                .filter(c -> !"active".equalsIgnoreCase(c.getStatus()))
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

    public List<CompanyDTO> searchByName(String keyword) {
        return companyRepository.findByName(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CompanyDTO> searchByNameContaining(String keyword) {
        return companyRepository.findAll().stream()
                .filter(c -> c.getName().contains(keyword))
                .map(this::toDTO)// 부분 일치
                .toList();
    }
}
