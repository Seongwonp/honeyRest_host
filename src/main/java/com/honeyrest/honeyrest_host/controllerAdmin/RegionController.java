package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.RegionDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    // 대분류(시/도 등) 조회: level=1 같은 식
    @Operation(summary = "대분류 조회")
    @GetMapping
    public List<RegionDTO> listByLevel(@RequestParam Integer level){
        return regionService.listByLevel(level);
    }

    @Operation(summary = "하위 지역 조회")
    // 하위 지역 조회: parentId 기준
    @GetMapping("/{parentId}/children")
    public List<RegionDTO> children(@PathVariable Integer parentId){
        return regionService.listSubRegions(parentId);
    }
}
