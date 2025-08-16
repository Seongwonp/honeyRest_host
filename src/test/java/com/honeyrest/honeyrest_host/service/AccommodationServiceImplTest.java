package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
import lombok.extern.log4j.Log4j2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import com.honeyrest.honeyrest_host.entity.Company;
import com.honeyrest.honeyrest_host.entity.AccommodationCategory;
import com.honeyrest.honeyrest_host.entity.Region;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationCategoryRepository;
import com.honeyrest.honeyrest_host.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class AccommodationServiceImplTest {

    @Autowired
    private AccommodationService accommodationService;
    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private AccommodationCategoryRepository accommodationCategoryRepository;
    @Autowired
    private RegionRepository regionRepository;

    private Long companyId;
    private Long categoryId;
    private Long mainRegionId;
    private Long subRegionId;

    @BeforeEach
    void setUpPrerequisites() {
        Company company = companyRepository.save(Company.builder().name("테스트업체").businessNumber("123-45-67890").build());
        AccommodationCategory category = accommodationCategoryRepository.save(AccommodationCategory.builder().name("호텔").build());
        Region main = regionRepository.save(Region.builder().name("대구").build());
        Region sub = regionRepository.save(Region.builder().name("중구").build());

        companyId = company.getCompanyId();
        categoryId = category.getCategoryId();
        mainRegionId = main.getRegionId();
        subRegionId = sub.getRegionId();
        assertNotNull(companyId);
        assertNotNull(categoryId);
        assertNotNull(mainRegionId);
        assertNotNull(subRegionId);
    }

    @Test
    @Commit
    void registerAccommodation() {
        AccommodationDTO req = AccommodationDTO.builder()
                .companyId(companyId)
                .categoryId(categoryId)
                .mainRegionId(mainRegionId)
                .subRegionId(subRegionId)
                .name("테스트 호텔1")
                .address("대구 중구")
                .description("테스트용 호텔입니다")
                .status("ACTIVE")
                .build();

        AccommodationDTO savedDto = accommodationService.registerAccommodation(req);

        assertNotNull(savedDto.getAccommodationId(),"숙소 id 가 생성되지 않았습니다.");
        Accommodation saved = accommodationRepository.findById(savedDto.getAccommodationId())
                .orElseThrow(() -> new AssertionError("저장 직후 재조회 실패: ID=" + savedDto.getAccommodationId()));

        assertEquals("테스트 호텔1", saved.getName());
    }
}