package com.backend.backend.dto;

import com.backend.backend.entities.TimeEntry;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TimeEntryDto(
        Long id,
        String reportReference,
        String reportTitle,
        LocalDate entryDate,
        BigDecimal hoursSpent,
        String comment
) {
    public static TimeEntryDto from(TimeEntry entry) {
        return new TimeEntryDto(
                entry.getId(),
                entry.getReport().getReference(),
                entry.getReport().getTitle(),
                entry.getEntryDate(),
                entry.getHoursSpent(),
                entry.getComment()
        );
    }
}
