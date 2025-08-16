package com.honeyrest.honeyrest_host.dto;

import com.honeyrest.honeyrest_host.entity.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminPrincipalDTO {
    private final Long userId;
    private final RoleType roleType;
    private final String email;

}
