package com.backend.backend.dto;

import com.backend.backend.Enums.ReportStatus;

import java.util.Map;

public record DashboardOverviewDto(
        String enterpriseName,
        Map<ReportStatus, Long> reportsByStatus,
        long totalReports
) {}
