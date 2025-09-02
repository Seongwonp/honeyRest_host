package com.honeyrest.honeyrest_host.service;


import com.honeyrest.honeyrest_host.dto.CompanyDTO;
import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.entity.Company;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import com.honeyrest.honeyrest_host.repository.ReservationRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final AccommodationRepository accommodationRepository;
    private final ReservationRepository reservationRepository;

    // == mapper ==
    private CompanyDTO toDTO(Company e) {
        if (e == null) return null;
        return CompanyDTO.builder()
                .companyId(e.getCompanyId())
                .name(e.getName())
                .businessNumber(e.getBusinessNumber())
                .ownerName(e.getOwnerName())
                .phone(e.getPhone())
                .email(e.getEmail())
                .address(e.getAddress())
                .bankInfo(e.getBankInfo())
                .commissionRate(e.getCommissionRate())
                .status(e.getStatus())
                .build();
    }

    private Company toEntity(CompanyDTO d) {
        if (d == null) return null;
        return Company.builder()
                .companyId(d.getCompanyId())
                .name(d.getName())
                .businessNumber(d.getBusinessNumber())
                .ownerName(d.getOwnerName())
                .phone(d.getPhone())
                .email(d.getEmail())
                .address(d.getAddress())
                .bankInfo(d.getBankInfo())
                .commissionRate(d.getCommissionRate())
                .status(d.getStatus())
                .build();
    }

    @Override
    public CompanyDTO create(CompanyDTO dto) {
        // 사업자 번호 중복 체크
        if (dto.getBusinessNumber() != null && companyRepository.existsByBusinessNumber(dto.getBusinessNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 등록된 사업자번호입니다.");
        }
        Company entity = toEntity(dto);
        Company saved = companyRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDTO getById(Long id) {
        Company e = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("업체가 존재하지 않습니다."));
        return toDTO(e);
    }

    @Override
    public List<CompanyDTO> getAll() {
        return companyRepository.findAll().stream().map(this::toDTO).toList();
    }


    @Override
    public CompanyDTO update(Long id, CompanyDTO dto) {
        Company cur = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("업체가 존재하지 않습니다."));

        Company updated = Company.builder()
                .companyId(cur.getCompanyId())
                .name(dto.getName() != null ? dto.getName() : cur.getName())
                .businessNumber(dto.getBusinessNumber() != null ? dto.getBusinessNumber() : cur.getBusinessNumber())
                .ownerName(dto.getOwnerName() != null ? dto.getOwnerName() : cur.getOwnerName())
                .phone(dto.getPhone() != null ? dto.getPhone() : cur.getPhone())
                .email(dto.getEmail() != null ? dto.getEmail() : cur.getEmail())
                .address(dto.getAddress() != null ? dto.getAddress() : cur.getAddress())
                .bankInfo(dto.getBankInfo() != null ? dto.getBankInfo() : cur.getBankInfo())
                .commissionRate(dto.getCommissionRate() != null ? dto.getCommissionRate() : cur.getCommissionRate())
                .status(dto.getStatus() != null ? dto.getStatus() : cur.getStatus())
                .build();

        return toDTO(companyRepository.save(updated));
    }

    @Override
    public void delete(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new EntityNotFoundException("업체가 존재하지 않습니다.");
        }
        companyRepository.deleteById(id);
    }

    @Override
    public CompanyDTO getByUserEmail(String email) {
        return toDTO(companyRepository.findCompanyByEmail(email));
    }

    @Override
    public Long getCompanyIdByUserEmail(String email) {
        return companyRepository.findCompanyIdByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("업체 아아디인 이메일을 찾을 수 없습니다" +
                                                                "."));
    }

    @Override
    public Long getCompanyIdByOfCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalArgumentException("사용자권한없음.");
        return getCompanyIdByUserEmail(auth.getName()); // 로그인 username(email)로 회사 ID 조회
    }

    @Override
    public Long getCompanyIdByAccommodationId(Long accommodationId) {
        return accommodationRepository.findCompanyIdByAccommodationId(accommodationId);
    }

}
