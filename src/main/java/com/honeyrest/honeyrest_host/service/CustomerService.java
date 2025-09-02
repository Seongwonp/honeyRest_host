package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.UserDetailDTO;
import com.honeyrest.honeyrest_host.dto.UserListDTO;
import org.springframework.data.domain.Page;

public interface CustomerService {
    Page<UserListDTO> list(String q, Integer page, Integer size, String sort);

    UserDetailDTO getDetail(Long id);
}
