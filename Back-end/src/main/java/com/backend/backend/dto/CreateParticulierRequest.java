package com.backend.backend.dto;

public record CreateParticulierRequest(
        String firstName,
        String lastName,
        String email,
        String phone
) {}
