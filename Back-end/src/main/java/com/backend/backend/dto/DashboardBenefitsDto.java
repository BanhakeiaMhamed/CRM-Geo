package com.backend.backend.dto;

import java.util.List;

public record DashboardBenefitsDto(
        int year,
        List<EngineerBenefitDto> engineers
) {}
