package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.dtoOwner.RegionDTO;
import com.honeyrest.honeyrest_host.repositoryOwner.ORegionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ORegionService {
    private final ORegionRepository regionRepository;
    private final ModelMapper modelMapper;

    public RegionDTO getRegion(Long id){
        return modelMapper.map(regionRepository.findById(id), RegionDTO.class);
    }
    public List<RegionDTO> getAllRegions(){
        return regionRepository.findAll().stream().map(r -> modelMapper.map(r,RegionDTO.class)).toList();
    }
}
