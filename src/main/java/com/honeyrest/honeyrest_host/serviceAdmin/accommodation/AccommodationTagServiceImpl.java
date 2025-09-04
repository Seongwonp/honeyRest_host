package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;


import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationTagDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.AccommodationTag;
import com.honeyrest.honeyrest_host.entity.AccommodationTagMap;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationTagMapRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationTagRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AccommodationTagServiceImpl implements AccommodationTagService {

    private final AccommodationTagRepository tagRepo;
    private final AccommodationTagMapRepository mapRepo;
    private final ModelMapper modelMapper;
    private final EntityManager em;


    @Override
    @Transactional(readOnly = true)
    public List<AccommodationTagDTO> findAll() {
        return tagRepo.findAll(Sort.by("category", "name").ascending())
                .stream()
                .map(t -> new AccommodationTagDTO(t.getTagId(), t.getName(), t.getCategory()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<AccommodationTagDTO>> findAllGroupedByCategory() {
        return findAll().stream()
                .collect(Collectors.groupingBy(
                        AccommodationTagDTO::getCategory,
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccommodationTagDTO> findByAccommodationId(Long accommodationId) {
        return tagRepo.findByAccommodationIdOrderByCategoryAscNameAsc(accommodationId)
                .stream()
                .map(t -> new AccommodationTagDTO(t.getTagId(), t.getName(), t.getCategory()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccommodationTagDTO> findByIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return List.of();
        return tagRepo.findByTagIdInOrderByCategoryAscNameAsc(tagIds)
                .stream()
                .map(t -> new AccommodationTagDTO(t.getTagId(), t.getName(), t.getCategory()))
                .toList();
    }

    @Override
    public void replaceMapping(Long accommodationId, List<Long> tagIds) {
        // 1) 기존 매핑 삭제 — 메서드명 정확히 일치
        mapRepo.deleteByAccommodation_AccommodationId(accommodationId);

        // 2) 신규 매핑 저장
        if (tagIds == null || tagIds.isEmpty()) return;

        // 프록시 참조(쿼리 추가 발생 없이 FK만 세팅)
        Accommodation accRef = em.getReference(Accommodation.class, accommodationId);

        List<AccommodationTagMap> maps = tagIds.stream()
                .distinct()
                .map(tid -> AccommodationTagMap.builder()
                        .accommodation(accRef)
                        .tag(em.getReference(AccommodationTag.class, tid))
                        .build())
                .toList();

        mapRepo.saveAll(maps);
    }
}