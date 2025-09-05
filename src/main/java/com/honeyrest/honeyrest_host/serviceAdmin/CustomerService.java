package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.UserDetailDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.UserListDTO;
import org.springframework.data.domain.Page;

public interface CustomerService {
    Page<UserListDTO> list(String q, Integer page, Integer size, String sort);

    UserDetailDTO getDetail(Long id);
}
