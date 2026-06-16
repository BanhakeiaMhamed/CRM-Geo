package com.backend.backend.dto;

public record CreateProfesionnelRequest(
        String companyName,
        String siren,
        String siret,
        String companyType,
        String email,
        String phone
) {}
