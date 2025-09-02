package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoOwner.UserDTO;
import com.honeyrest.honeyrest_host.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
public class UserController {

    private final UserService userService;

    @GetMapping("/user/list")
    public String userList(
            @ModelAttribute PageRequestDTO pageRequestDTO,
            Model model) {
        PageResponseDTO<UserDTO> pageResponseDTO = userService.getUsersWithPage(pageRequestDTO);

        model.addAttribute("users", pageResponseDTO.getDtoList());
        model.addAttribute("pageResponseDTO" , pageResponseDTO);
        return "/owner/user/list";
    }
}
