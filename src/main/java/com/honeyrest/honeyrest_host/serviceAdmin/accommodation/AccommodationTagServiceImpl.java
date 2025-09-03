package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;


import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationTagDTO;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationTagRepository;
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

    private final AccommodationTagRepository accommodationTagRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<AccommodationTagDTO> findAll() {
        return accommodationTagRepository.findAll(Sort.by("category","name").ascending())
                .stream()
                .map(t -> new AccommodationTagDTO(t.getTagId(), t.getName(), t.getCategory()))
                .toList();
    }


    @Override
    public Map<String, List<AccommodationTagDTO>> findAllGroupedByCategory() {
        return findAll().stream()
                .collect(Collectors.groupingBy(
                        AccommodationTagDTO::getCategory,
                        TreeMap::new,             // 카테고리명 정렬(람다)
                        Collectors.toList()
                ));
    }

}
