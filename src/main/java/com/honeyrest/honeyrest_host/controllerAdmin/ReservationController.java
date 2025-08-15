package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReservationDTO;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ModelMapper modelMapper;

    @GetMapping("/number/{number}")
    public ResponseEntity<ReservationDTO> getByNumber(@PathVariable String number) {
        ReservationDTO dto = reservationService.getReservationByNumber(number);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<ReservationDTO>> listByStatus(@PathVariable(required = false) String status,
                                                                        @ModelAttribute PageRequestDTO pageRequestDTO) {
        var response = reservationService.getReservationsByStatus(status, pageRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> update(@PathVariable Long reservationId, @RequestBody ReservationDTO request) {
        // 서비스가 엔티티를 반환

        request.setReservationId(reservationId);
        Reservation updated = reservationService.updateReservation(request);
        ReservationDTO response = modelMapper.map(updated, ReservationDTO.class);
        return ResponseEntity.ok(response);

    }

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long reservationId, @RequestBody ReservationDTO req) {
        reservationService.canceledReservation(reservationId, req.getCancelReason());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> creat(@RequestBody ReservationDTO request) {
        ReservationDTO created = reservationService.createReservation(request);
        return ResponseEntity.ok(created);
    }


}
