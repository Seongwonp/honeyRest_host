package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email);


        // 비활성/권한 체크는 Provider/Handler에서 추가로 해도 됨
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPasswordHash())              // BCrypt 저장된 값
                .roles(u.getRole())              // ROLE_COMPANY_ADMIN 등으로 매핑됨
                .accountLocked(false)
                .disabled(false)
                .build();
    }
}