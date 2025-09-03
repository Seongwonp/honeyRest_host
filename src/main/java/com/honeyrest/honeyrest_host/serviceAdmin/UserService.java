package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dto.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class UserService {

    private final UserRepository userRepository;

    // ========= toDto ==========

    private AdminLoginRequestDTO toDto(User user) {
        return AdminLoginRequestDTO.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .build();
    }

    public AdminLoginRequestDTO getUserByEmail(String email) {
        return toDto(userRepository.findByEmail(email));
    }
}
