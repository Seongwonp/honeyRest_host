package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.CompanyDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/companies")
public class CompanyAPIController {

    private final CompanyService companyService;

    @Operation(summary = "업체 등록")
    @PostMapping
    public ResponseEntity<CompanyDTO> create(@Valid @RequestBody CompanyDTO req) {
        CompanyDTO saved = companyService.create(req);
        return ResponseEntity
                .created(URI.create("/api/admin/companies/" + saved.getCompanyId()))
                .body(saved);
    }

    @Operation(summary = "업체 전체 조회")
    @GetMapping
    public List<CompanyDTO> list() {
        return companyService.getAll();
    }

    @Operation(summary = "업체 단건 조회")
    @GetMapping("/{id}")
    public CompanyDTO get(@PathVariable Long id) {
        return companyService.getById(id);
    }

    @Operation(summary = "업체 수정(전체/부분)")
    @PutMapping("/{id}")
    public CompanyDTO update(@PathVariable Long id, @RequestBody CompanyDTO req) {
        return companyService.update(id, req);
    }

    @Operation(summary = "업체 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
