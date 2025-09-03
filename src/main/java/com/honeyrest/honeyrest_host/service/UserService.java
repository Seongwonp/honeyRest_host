package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dtoOwner.AccommodationDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoOwner.UserDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public PageResponseDTO<UserDTO> getUsersWithPage(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("lastLogin").descending());

        Page<User> page;

        page = userRepository.findAll(pageable);

        List<UserDTO> list = page.getContent().stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();

        long total = page.getTotalElements();

        PageResponseDTO<UserDTO> responseDTO = PageResponseDTO.<UserDTO>withAll()
                .dtoList(list)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(total)
                .build();

        return responseDTO;
    }

    public UserDTO getUserByNameAndPhone(String name, String phone) {
        return modelMapper.map(userRepository.findByNameAndPhone(name, phone), UserDTO.class);
    }

    public List<UserDTO> searchByNameContaining(String name) {
        return userRepository.findByNameContainingIgnoreCase(name).stream().map(u -> modelMapper.map(u, UserDTO.class)).toList();

    }
}
