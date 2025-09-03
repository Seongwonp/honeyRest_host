package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dto.RegionDTO;
import com.honeyrest.honeyrest_host.repositoryAdmin.RegionRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionRepository regionRepository;

    // 대분류(시/도 등) 조회: level=1 같은 식
    @Operation(summary = "대분류 조회")
    @GetMapping
    public List<RegionDTO> listByLevel(@RequestParam Integer level){
        return regionRepository.findByLevel(level).stream()
                .map(RegionDTO::of).toList();
    }

    @Operation(summary = "하위 지역 조회")
    // 하위 지역 조회: parentId 기준
    @GetMapping("/{parentId}/children")
    public List<RegionDTO> children(@PathVariable Long parentId){
        return regionRepository.findByParentId(parentId).stream()
                .map(RegionDTO::of).toList();
    }
}
