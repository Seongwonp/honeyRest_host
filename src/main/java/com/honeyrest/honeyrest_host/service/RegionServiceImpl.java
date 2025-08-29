package com.honeyrest.honeyrest_host.service;


import com.honeyrest.honeyrest_host.dto.RegionDTO;
import com.honeyrest.honeyrest_host.entity.Region;
import com.honeyrest.honeyrest_host.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class RegionServiceImpl implements RegionService {
    private final RegionRepository regionRepository;



    @Override
    public List<RegionDTO> listMainRegions() {
        List<Region> regions = regionRepository.findByLevel(1);
        return regions.stream().map(RegionDTO::of).toList();
    }

    @Override
    public List<RegionDTO> listSubRegions(Long parentId) {
        List<Region> regions = regionRepository.findByParentId(parentId);
        return regions.stream().map(RegionDTO::of).toList();
    }
}
