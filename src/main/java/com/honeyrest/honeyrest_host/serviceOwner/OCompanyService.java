package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.type.CompanyStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dtoOwner.CompanyDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.domain.entity.Accommodation;
import com.honeyrest.domain.entity.Company;
import com.honeyrest.honeyrest_host.repositoryOwner.OAccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OCompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class OCompanyService {
    private final OCompanyRepository companyRepository;
    private final ObjectMapper objectMapper;
    private final OAccommodationRepository accommodationRepository;

    /**
     * company.bank_info 는 JSON 객체 {"bank","account","holder"} 로 저장된다(사용자 API·시드와 같은 형식).
     * 과거 코드는 JSON 배열(List&lt;String&gt;)로 읽으려다 매번 파싱 실패 경고를 남기고 빈 값을 보여 줬고,
     * 저장할 때는 JSON 이 아닌 "a, b, c" 문자열을 JSON 컬럼에 넣었다.
     * 화면에서는 "은행, 계좌번호, 예금주" 한 줄로 보여 주고 같은 형식으로 입력받는다.
     */
    String bankInfoToText(String json) {
        if (json == null || json.isBlank()) return "";
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node.isObject()) {
                return Stream.of("bank", "account", "holder")
                        .map(k -> node.path(k).asText(""))
                        .filter(v -> !v.isBlank())
                        .collect(Collectors.joining(", "));
            }
            if (node.isArray()) {
                List<String> parts = new ArrayList<>();
                node.forEach(n -> parts.add(n.asText()));
                return String.join(", ", parts);
            }
            return node.asText();
        } catch (JsonProcessingException e) {
            // JSON 이 아닌 과거 값은 그대로 보여 준다
            return json;
        }
    }

    String bankInfoToJson(String text) {
        if (text == null || text.isBlank()) return null;
        String trimmed = text.trim();
        // 이미 JSON 객체면 그대로 저장 (형식 검증만)
        if (trimmed.startsWith("{")) {
            try {
                return objectMapper.writeValueAsString(objectMapper.readTree(trimmed));
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("은행 정보 JSON 형식이 올바르지 않습니다.");
            }
        }
        String[] parts = trimmed.split("\\s*,\\s*");
        Map<String, String> map = new LinkedHashMap<>();
        map.put("bank", parts.length > 0 ? parts[0] : "");
        map.put("account", parts.length > 1 ? parts[1] : "");
        map.put("holder", parts.length > 2 ? String.join(", ", Arrays.copyOfRange(parts, 2, parts.length)) : "");
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
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
                .bankInfo(bankInfoToText(company.getBankInfo()))
                .businessNumber(company.getBusinessNumber())
                .commissionRate(company.getCommissionRate())
                .createdAt(company.getCreatedAt())
                .build();
    }

    private Company toEntity(CompanyDTO dto) {
        String status = dto.getStatus() == null || dto.getStatus().isBlank()
                ? CompanyStatus.ACTIVE : dto.getStatus().trim().toUpperCase(Locale.ROOT);
        return Company.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .companyId(dto.getCompanyId())
                .status(status)
                .bankInfo(bankInfoToJson(dto.getBankInfo()))
                .businessNumber(dto.getBusinessNumber())
                .commissionRate(dto.getCommissionRate())
                .ownerName(dto.getOwnerName())
                .build();
    }

    public void registerCompany(CompanyDTO dto) {
        companyRepository.save(toEntity(dto));
    }

    /** 비활성 업체를 다시 활성화한다 (비활성 업체 목록의 "활성화" 버튼) */
    public void activateCompany(Integer id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("업체가 존재하지 않습니다."));
        CompanyDTO dto = toDTO(company);
        dto.setStatus(CompanyStatus.ACTIVE);
        companyRepository.save(toEntity(dto));
    }

    public void removeCompany(Integer id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        CompanyDTO dto = toDTO(company);
        dto.setStatus(CompanyStatus.INACTIVE);  // 상태를 변경

        companyRepository.save(toEntity(dto)); // 업데이트
    }

    public void modifyCompany(CompanyDTO dto) {
        companyRepository.save(toEntity(dto));
    }

    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::toDTO)
                .filter(c -> CompanyStatus.isActive(c.getStatus()))
                .toList();
    }

    public CompanyDTO getCompany(Integer companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("업체가 존재하지 않습니다."));
        return toDTO(company);
    }

    public PageResponseDTO<CompanyDTO> getCompaniesWithPage(PageRequestDTO requestDTO){
        return getCompaniesWithPage(requestDTO, null);
    }

    /** 활성 업체 목록. keyword 는 업체명·대표자명 부분 일치 (목록 상단 검색창) */
    public PageResponseDTO<CompanyDTO> getCompaniesWithPage(PageRequestDTO requestDTO, String keyword){
        String kw = keyword == null || keyword.isBlank() ? null : keyword.trim();

        Pageable pageable = PageRequest.of(requestDTO.getPage() - 1, requestDTO.getSize(), Sort.by("companyId").descending());

        // 상태 조건을 쿼리에 넣는다. 페이지를 읽은 뒤 메모리에서 거르면 "총 N개"와 페이지 수가 실제 행과 어긋났다.
        Page<Company> page = companyRepository.findActive(kw, pageable);

        List<CompanyDTO> dtoList = page.getContent().stream()
                .map(this::toDTO)
                .toList();

        return PageResponseDTO.<CompanyDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(requestDTO)
                .totalCount(page.getTotalElements())
                .build();
    }


    public PageResponseDTO<CompanyDTO> getInActiveCompaniesWithPage(PageRequestDTO requestDTO){

        Pageable pageable = PageRequest.of(requestDTO.getPage() - 1, requestDTO.getSize(), Sort.by("companyId").descending());

        Page<Company> page = companyRepository.findInactive(pageable);

        List<CompanyDTO> dtoList = page.getContent().stream()
                .map(this::toDTO)
                .toList();

        return PageResponseDTO.<CompanyDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(requestDTO)
                .totalCount(page.getTotalElements())
                .build();
    }

    public Integer getCompanyIdByAccommodationId(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId);
        return accommodation.getCompany().getCompanyId();
    }

    public List<CompanyDTO> searchByName(String keyword) {
        return companyRepository.findByName(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CompanyDTO> searchByNameContaining(String keyword) {
        return companyRepository.findAll().stream()
                .filter(c -> keyword == null || keyword.isBlank()
                        || (c.getName() != null && c.getName().contains(keyword))
                        || (c.getOwnerName() != null && c.getOwnerName().contains(keyword)))
                .map(this::toDTO)// 부분 일치
                .toList();
    }
}
