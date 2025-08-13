package com.honeyrest.honeyrest_host.controller;

import com.honeyrest.honeyrest_host.dto.RoomDTO;
import com.honeyrest.honeyrest_host.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public String list(@RequestParam Long accommodationId, Model model) {
        model.addAttribute("rooms", roomService.getByRoomId(accommodationId));
        model.addAttribute("accommodationId", accommodationId);
        return "admin/rooms/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam Long accommodationId, Model model) {
        RoomDTO dto = new RoomDTO();
        dto.setAccommodationId(accommodationId);
        model.addAttribute("room", dto);
        return "admin/rooms/form";
    }

    @PostMapping
    public String create(@ModelAttribute RoomDTO dto) {
        roomService.registerRoom(dto);
        return "redirect:/admin/rooms?accommodationId=" + dto.getAccommodationId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("room", roomService.getByRoomId(id));
        return "admin/rooms/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute RoomDTO dto) {
        roomService.modifyRoom(dto);
        return "redirect:/admin/rooms?accommodationId=" + dto.getAccommodationId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @RequestParam Long accommodationId) {
        roomService.removeRoom(id);
        return "redirect:/admin/rooms?accommodationId=" + accommodationId;
    }
}
