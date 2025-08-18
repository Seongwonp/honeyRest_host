package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReviewDTO;
import com.honeyrest.honeyrest_host.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/admin/reviews")
public class ReviewAPIController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<ReviewDTO>> list(
            @Parameter(description = "리뷰 상태 필터") @RequestParam(required = false) String status,
            @Parameter(description = "객실 ID 필터") @RequestParam(required = false) Long roomId,
            @Parameter(description = "숙소 ID 필터") @RequestParam(required = false) Long accommodationId,
            @Parameter(description = "정렬: ratingDesc | ratingAsc | (기본: 최신순)") @RequestParam(required = false) String sort,
            @Parameter(description = "page/size/sortBy는 DTO 내부 규칙 사용") PageRequestDTO pageRequestDTO
    ) {
        var page = reviewService.getList(status, roomId, accommodationId, sort, pageRequestDTO);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "리뷰 단건 조회")
    @GetMapping("{reviewId}")
    public ResponseEntity<ReviewDTO> getOne(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getOne(reviewId));
    }

    @Operation(summary = "리뷰 등록")
    @PostMapping
    public ResponseEntity<ReviewDTO> create(@RequestBody ReviewDTO reviewDTO) {
        var created = reviewService.insert(reviewDTO);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "리뷰 수정")
    @PutMapping("{reviewId}")
    public ResponseEntity<ReviewDTO> update(@PathVariable Long reviewId, @Valid @RequestBody ReviewDTO reviewDTO) {
        var updated = reviewService.update(reviewId, reviewDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "리뷰 상태/ 답글 부분 수정")
    @PatchMapping("{reviewId}")
    public ResponseEntity<ReviewDTO> patch(@PathVariable Long reviewId, @RequestBody ReviewDTO reviewDTO) {
        ReviewDTO updated = reviewService.patch(reviewId, reviewDTO);
        return ResponseEntity.ok(updated);

    }
}
