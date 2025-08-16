package com.honeyrest.honeyrest_host.security;

import com.honeyrest.honeyrest_host.entity.enums.RoleType;

import java.io.Serializable;

public record AdminPrincipal(Long userId, String email, RoleType role) implements Serializable {}

