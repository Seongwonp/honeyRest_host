package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;


import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationTagDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.AccommodationTag;
import com.honeyrest.honeyrest_host.entity.AccommodationTagMap;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationTagMapRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AccommodationTagServiceImpl implements AccommodationTagService {

    private final AccommodationTagRepository tagRepo;
    private final AccommodationTagRepository accommodationTagRepository;
    private final AccommodationTagMapRepository tagMapRepository;
    private final AccommodationRepository accommodationRepository;


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
        // 모두 삭제 후 다시 저장
        tagMapRepository.deleteByAccommodation_AccommodationId(accommodationId);

        if (tagIds == null || tagIds.isEmpty()) return;

        // null / 중복 제거
        List<Long> cleanIds = tagIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (cleanIds.isEmpty()) return;

        // 존재하는 태그만 조회
        List<AccommodationTag> tags = accommodationTagRepository.findAllById(cleanIds);
        if (tags.isEmpty()) return;

        Accommodation acc = accommodationRepository.getReferenceById(accommodationId);
        for (AccommodationTag t : tags) {
            AccommodationTagMap map = AccommodationTagMap.builder()
                    .accommodation(acc)
                    .tag(t)
                    .build();
            tagMapRepository.save(map);
        }
    }


}