package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoOwner.UserDTO;
import com.honeyrest.honeyrest_host.serviceOwner.OUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller("ownerUserController")
@RequiredArgsConstructor
@RequestMapping("/owner")
public class UserController {

    private final OUserService userService;

    @GetMapping("/user/list")
    public String userList(
            @ModelAttribute PageRequestDTO pageRequestDTO,
            Model model) {
        PageResponseDTO<UserDTO> pageResponseDTO = userService.getUsersWithPage(pageRequestDTO);

        model.addAttribute("users", pageResponseDTO.getDtoList());
        model.addAttribute("pageResponseDTO" , pageResponseDTO);
        return "/owner/user/list";
    }

    @GetMapping("/user/search")
    @ResponseBody
    public List<UserDTO> userSearch(@RequestParam String keyword) {
        return userService.searchByNameContaining(keyword);
    }
}
