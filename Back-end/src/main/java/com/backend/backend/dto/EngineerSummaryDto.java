package com.backend.backend.dto;

import com.backend.backend.Enums.Role;
import com.backend.backend.entities.Engineer;

public record EngineerSummaryDto(
        String email,
        String firstName,
        String lastName,
        Role role
) {
    public static EngineerSummaryDto from(Engineer engineer) {
        return new EngineerSummaryDto(
                engineer.getEmail(),
                engineer.getFirstName(),
                engineer.getLastName(),
                engineer.getRole()
        );
    }
}
