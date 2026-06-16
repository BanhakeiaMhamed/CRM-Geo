package com.backend.backend.dto;

import com.backend.backend.Enums.Role;

public record UserProfileDto(
        String email,
        String firstName,
        String lastName,
        Role role,
        String enterpriseName
) {}
