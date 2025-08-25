package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.RoomDTO;
import com.honeyrest.honeyrest_host.dto.RoomImageDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.service.RoomImageService;
import com.honeyrest.honeyrest_host.service.RoomService;
import com.honeyrest.honeyrest_host.util.FileUploadUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/rooms")
public class RoomPageController {
    private final RoomService roomService;
    private final AccommodationRepository accommodationRepository;
    private final FileUploadUtil fileUploadUtil;
    private final RoomImageService roomImageService;


    /** 전체 객실 목록 (사이드바 진입) */
    @GetMapping("/list_all")
    public String listAll(@PageableDefault(size = 10, sort = "roomId", direction = Sort.Direction.DESC)
                          Pageable pageable,
                          Model model) {
        Page<RoomDTO> page = roomService.findPageAll(pageable);
        model.addAttribute("page", page);
        return "admin/rooms/list_all";
    }
    /** 라우팅 헬퍼: /list?accommodationId=... -> by-accommodation, 없으면 list_all */
    @GetMapping("/list")
    public String listRouter(@RequestParam(required = false) Long accommodationId) {
        if (accommodationId != null) {
            return "redirect:/admin/rooms/list/by-accommodation?accommodationId=" + accommodationId;
        }
        return "redirect:/admin/rooms/list_all";
    }


    /** 특정 숙소의 객실 목록 (등록/수정 후 이동) */
    @GetMapping("/list/by-accommodation")
    public String listByAccommodation(@RequestParam Long accommodationId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "roomId"));
        model.addAttribute("page", roomService.findPageByAccommodationId(accommodationId, pageable));
        model.addAttribute("accommodationId", accommodationId);
        return "admin/rooms/list"; // 숙소별 테이블
    }

    /** 등록 폼 */
    @GetMapping("/add")
    public String addForm(@RequestParam(value = "accommodationId", required = false) Long accommodationId,
                          Model model) {
        RoomDTO form = new RoomDTO();
        form.setAccommodationId(accommodationId);
        model.addAttribute("form", form);
        model.addAttribute("accommodations", accommodationRepository.findAll());
        return "admin/rooms/add";
    }

    /** 등록 처리 */
    @PostMapping("/add")
    public String create(@Valid @ModelAttribute("form") RoomDTO form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            // 에러 시 다시 렌더링할 데이터들 채워줌
            model.addAttribute("accommodations", accommodationRepository.findAll());
            return "admin/rooms/add";
        }
        RoomDTO saved = roomService.registerRoom(form);
        ra.addFlashAttribute("msg", "객실이 등록되었습니다.");
        // ✅ 숙소별 목록으로 리다이렉트
        return "redirect:/admin/rooms/list/by-accommodation?accommodationId=" + saved.getAccommodationId();
    }
    /* ========== 수정 ========== */
    // 수정 폼
    @GetMapping("/edit/{roomId}")
    public String editForm(@PathVariable Long roomId, Model model) {
        RoomDTO form = roomService.getByRoomId(roomId); // 반드시 null 아니게
        model.addAttribute("form", form);
        model.addAttribute("accommodations", accommodationRepository.findAll());
        return "admin/rooms/edit"; // templates/admin/rooms/edit.html
    }

    @PostMapping("/{roomId}")
    public String update(@PathVariable Long roomId,
                         @Valid @ModelAttribute("form") RoomDTO form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) throws Exception {
        // 이미지 업로드
       String image =  fileUploadUtil.upload(form.getImage(),"room");

        if (binding.hasErrors()) {
            model.addAttribute("accommodations", accommodationRepository.findAll());
            return "admin/rooms/edit";
        }

        // form에 roomId 보장
        form.setRoomId(roomId);

        roomService.modifyRoom(form);

        RoomImageDTO roomImageDTO = RoomImageDTO.builder()
                .image(image).build();
        roomImageService.registerRoomImage(roomImageDTO);
        // 알림용 플래시 메시지
        ra.addFlashAttribute("msg", "객실이 수정되었습니다.");

        // ✅ 전체 객실 목록으로 이동
        return "redirect:/admin/rooms/list_all";
    }

    /* ========== 삭제 ========== */
    @PostMapping("/{roomId}/delete")
    public String delete(@PathVariable Long roomId,
                         @RequestParam("accommodationId") Long accommodationId,
                         RedirectAttributes ra) {
        roomService.removeRoom(roomId);
        ra.addFlashAttribute("msg", "객실이 삭제되었습니다.");
        return "redirect:/admin/rooms/list?accommodationId=" + accommodationId;
    }
}
