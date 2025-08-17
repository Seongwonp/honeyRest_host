package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Tag(name = "Reservation API", description = "예약 생성/조회/수정/취소 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationAPIController {

    private final ReservationService reservationService;
    private final org.modelmapper.ModelMapper modelMapper;

    @Operation(
            summary = "예약번호로 단건 조회",
            description = "예약번호(예: HR-2025-08-18-XXXXXX)로 예약 1건을 조회합니다."
    )
    @GetMapping("/number/{number}")
    public ResponseEntity<ReservationDTO> getByNumber(
            @Parameter(description = "예약번호", example = "HR-2025-08-18-ABC123") @PathVariable String number) {
        return ResponseEntity.ok(reservationService.getReservationByNumber(number));
    }

    @Operation(
            summary = "예약 목록 조회(페이징 + 상태 필터)",
            description =
                    """
                    상태(status)로 필터링 가능한 페이징 목록 조회입니다.
                    - status 미지정 시 전체
                    - 사용 예: /api/reservations?status=PENDING&page=1&size=10
                    """
    )
    @GetMapping
    public ResponseEntity<PageResponseDTO<ReservationDTO>> listByStatus(
            @Parameter(description = "예약 상태(PENDING, CONFIRMED, CANCELED 등). 생략 시 전체")
            @RequestParam(required = false) String status,
            @Parameter(description = "페이징 파라미터(page, size, sort 등)를 포함하는 DTO")
            PageRequestDTO pageRequestDTO
    ) {
        var response = reservationService.getReservationsByStatus(status, pageRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "예약 전체 목록 조회(비페이징)",
            description = "페이징 없이 모든 예약을 반환합니다. 데이터가 많으면 사용을 지양하세요."
    )
    @GetMapping("/all")
    public ResponseEntity<List<ReservationDTO>> getAllNoPaging() {
        return ResponseEntity.ok(reservationService.getAllReservationsNoPaging());
    }

    @Operation(
            summary = "예약 수정(전체 업데이트)",
            description = "경로의 reservationId 대상 예약을 요청 본문 값으로 갱신합니다. 부분수정이 필요하면 PATCH를 사용하세요."
    )
    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> update(
            @Parameter(description = "예약 ID", example = "1") @PathVariable Long reservationId,
            @Parameter(description = "수정할 예약 데이터") @RequestBody ReservationDTO request) {
        request.setReservationId(reservationId);
        Reservation updated = reservationService.updateReservation(request);
        return ResponseEntity.ok(modelMapper.map(updated, ReservationDTO.class));
    }

    @Operation(
            summary = "예약 취소(부분 업데이트)",
            description = "해당 예약을 취소 상태로 변경하고, 취소 사유(cancelReason)를 기록합니다."
    )
    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancel(
            @Parameter(description = "예약 ID", example = "1") @PathVariable Long reservationId,
            @Parameter(description = "취소 사유가 포함된 DTO (cancelReason 필드 사용)") @RequestBody ReservationDTO req) {
        reservationService.canceledReservation(reservationId, req.getCancelReason());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "예약 생성",
            description =
                    """
                    새 예약을 생성합니다.
                    필수: userId, roomId, checkInDate, checkOutDate, guestName, guestPhone, guestCount, price
                    - reservationNumber 미지정 시 서버에서 자동 생성
                    - status 미지정 시 PENDING
                    """
    )
    @PostMapping
    public ResponseEntity<ReservationDTO> create(
            @Parameter(description = "생성할 예약 데이터") @RequestBody ReservationDTO request) {
        var created = reservationService.createReservation(request);
        return ResponseEntity.status(201).body(created);
    }
}