package com.honeyrest.honeyrest_host.security;

import java.io.Serializable;

public record AdminPrincipal(Long userId, String email, String role) implements Serializable {}
