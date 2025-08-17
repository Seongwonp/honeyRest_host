package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dto.RoomDTO;
import com.honeyrest.honeyrest_host.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Rooms", description = "객실 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomAPIController {

    private final RoomService roomService;

    @Operation(summary = "숙소 id로 조회")
    @GetMapping
    public List<RoomDTO> listByAccommodation(@RequestParam Long accommodationId) {
        return roomService.findRoomsByAccommodationId(accommodationId);
    }

    @Operation(summary = "객실 id 조회")
    @GetMapping("/{roomId}")
    public RoomDTO get(@PathVariable Long roomId) {
        return roomService.getByRoomId(roomId);
    }

    @Operation(summary = "객실 등록")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDTO create(@RequestBody RoomDTO dto) {
        return roomService.registerRoom(dto);
    }

    @Operation(summary = "객실 수정")
    @PutMapping("/{roomId}")
    public RoomDTO update(@PathVariable Long roomId, @RequestBody RoomDTO dto) {
        dto.setRoomId(roomId);
        roomService.modifyRoom(dto);
        return roomService.getByRoomId(roomId);
    }

    @Operation(summary = "객실 삭제")
    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long roomId) {
        roomService.removeRoom(roomId);
    }
}