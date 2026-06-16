package com.backend.backend.controllers;

import com.backend.backend.dto.LogTimeRequest;
import com.backend.backend.dto.TimeEntryDto;
import com.backend.backend.entities.Engineer;
import com.backend.backend.metier.services.CurrentUserService;
import com.backend.backend.metier.services.TimeTrackingService;
import com.backend.backend.repositories.TimeEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeTrackingService timeTrackingService;
    private final TimeEntryRepository timeEntryRepository;
    private final CurrentUserService currentUserService;

    @GetMapping
    @Transactional(readOnly = true)
    public List<TimeEntryDto> myEntries() {
        Engineer engineer = currentUserService.getCurrentEngineer();
        return timeEntryRepository.findByEngineer(engineer).stream()
                .map(TimeEntryDto::from)
                .toList();
    }

    @PostMapping
    @Transactional
    public TimeEntryDto logTime(@RequestBody LogTimeRequest request) {
        Engineer engineer = currentUserService.getCurrentEngineer();
        return TimeEntryDto.from(timeTrackingService.logTime(
                request.reportReference(),
                engineer,
                request.entryDate(),
                request.hoursSpent(),
                request.comment()
        ));
    }

    @GetMapping("/report/{reference}/total")
    public BigDecimal reportTotal(@PathVariable String reference) {
        return timeTrackingService.totalHoursOnReport(reference);
    }
}
