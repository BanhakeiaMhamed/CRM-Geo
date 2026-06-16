package com.backend.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LogTimeRequest(
        String reportReference,
        LocalDate entryDate,
        BigDecimal hoursSpent,
        String comment
) {}
