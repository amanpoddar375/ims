package com.postanything.dto;

import com.postanything.enums.Role;

public record AuthResponse(Long id, String username, String email, Role role, String token) {}
