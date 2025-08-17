package com.honeyrest.honeyrest_host.controllerAdmin;

import com.honeyrest.honeyrest_host.dto.RoomDTO;
import com.honeyrest.honeyrest_host.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;                // ★ 바인딩 결과
import org.springframework.validation.annotation.Validated;   // ★ DTO 검증
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RoomPageController {

    @GetMapping("/admin/rooms")
    public String listPage(@RequestParam(required = false) Long accommodationId) {
        // accommodationId 없이 접근해도 화면은 열리지만, JS에서 필요 파라미터 검사
        return "admin/rooms/list";
    }

    @GetMapping("/admin/rooms/add")
    public String addPage(@RequestParam(required = false) Long accommodationId) {
        return "admin/rooms/add";
    }


}


//    // 숙소별 객실 목록
//    @GetMapping("/list")
//    public String listRooms(@RequestParam Long accommodationId, Model model) {
//        model.addAttribute("accommodationId", accommodationId);
//        return "admin/rooms/list";
//    }
//
//    // 객실 단건 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<RoomDTO> get(@PathVariable Long id) {
//        RoomDTO dto = roomService.getByRoomId(id);
//        return ResponseEntity.ok(dto);
//    }
//
//    // ===== 폼 진입: 쿼리파라미터 -> 경로변수로 변경 =====
//    // ex) /admin/rooms/accommodations/3/add
//    @GetMapping("/add")
//    public String addRoomForm(@RequestParam Long accommodationId, Model model) {
//        model.addAttribute("accommodationId", accommodationId);
//        model.addAttribute("room", new RoomDTO());
//        return "admin/rooms/add";
//    }
//
//    // 객실 등록
//    @PostMapping("/add")
//    public String register(@ModelAttribute("room") @Validated RoomDTO roomDTO, // ← DTO 제약(@NotNull 등) 적용
//                           BindingResult bindingResult,                         // ← 바인딩/검증 에러 수집
//                           Model model,
//                           RedirectAttributes redirectAttributes) {
//
//        // ★ 서버측 추가 유효성 검사(에러 발생 시 400 대신 폼으로 돌려보내기)
//        if (roomDTO.getAccommodationId() == null) {
//            bindingResult.rejectValue("accommodationId", "NotNull", "업체 ID(accommodationId)는 필수입니다.");
//        }
//        if (roomDTO.getName() == null || roomDTO.getName().isBlank()) {
//            bindingResult.rejectValue("name", "NotBlank", "객실 이름은 필수입니다.");
//        }
//        if (roomDTO.getPrice() == null) {
//            bindingResult.rejectValue("price", "NotNull", "가격은 필수입니다.");
//        }
//        if (roomDTO.getMaxOccupancy() == null) {
//            bindingResult.rejectValue("maxOccupancy", "NotNull", "최대 인원은 필수입니다.");
//        }
//
//        // 에러 있으면 그대로 add.html 로
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("room", roomDTO);
//            return "admin/rooms/add";
//        }
//
//        RoomDTO saved = roomService.registerRoom(roomDTO);
//        redirectAttributes.addFlashAttribute("message", "객실이 등록되었습니다 (Id = " + saved.getRoomId() + ")");
//        // ★ 리스트로 돌아갈 때도 확실한 숫자만 사용
//        return "redirect:/admin/rooms/list?accommodationId=" + saved.getAccommodationId();
//    }
//
//    @GetMapping("/{id}/edit")
//    public String editForm(@PathVariable Long id, Model model) {
//        model.addAttribute("room", roomService.getByRoomId(id));
//        return "admin/rooms/roomd-edit";
//    }
//
//    // 객실 부분 수정
//    @PatchMapping("/{id}")
//    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody RoomDTO dto) {
//        dto.setRoomId(id);
//        roomService.modifyRoom(dto);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        roomService.removeRoom(id);
//        return ResponseEntity.noContent().build();
//    }
//}