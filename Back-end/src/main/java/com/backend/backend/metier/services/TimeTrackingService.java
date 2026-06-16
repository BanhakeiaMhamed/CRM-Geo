package com.backend.backend.metier.services;

import com.backend.backend.entities.Engineer;
import com.backend.backend.entities.Report;
import com.backend.backend.entities.TimeEntry;
import com.backend.backend.repositories.TimeEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TimeTrackingService {

    private final TimeEntryRepository timeEntryRepository;
    private final ReportWorkflowService reportWorkflowService;

    @Transactional
    public TimeEntry logTime(String reportReference, Engineer engineer,
                             LocalDate date, BigDecimal hours, String comment) {
        Report report = reportWorkflowService.findByReference(reportReference);
        TimeEntry entry = TimeEntry.builder()
                .report(report)
                .engineer(engineer)
                .entryDate(date)
                .hoursSpent(hours)
                .comment(comment)
                .build();
        return timeEntryRepository.save(entry);
    }

    public BigDecimal totalHoursOnReport(String reportReference) {
        Report report = reportWorkflowService.findByReference(reportReference);
        return timeEntryRepository.sumHoursByReport(report);
    }

    public BigDecimal totalHoursForEngineer(Engineer engineer, LocalDate start, LocalDate end) {
        return timeEntryRepository.sumHoursByEngineerBetween(engineer, start, end);
    }
}
