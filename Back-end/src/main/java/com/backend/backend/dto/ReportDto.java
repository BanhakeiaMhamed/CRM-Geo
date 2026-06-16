package com.backend.backend.dto;

import com.backend.backend.Enums.ReportStatus;
import com.backend.backend.Enums.ReportType;
import com.backend.backend.entities.Report;
import com.backend.backend.entities.ReportTag;

import java.math.BigDecimal;
import java.util.List;

public record ReportDto(
        Long id,
        String reference,
        String title,
        ReportType reportType,
        ReportStatus status,
        BigDecimal revenueAmount,
        String engineerEmail,
        List<String> tags
) {
    public static ReportDto from(Report report) {
        return new ReportDto(
                report.getId(),
                report.getReference(),
                report.getTitle(),
                report.getReportType(),
                report.getStatus(),
                report.getRevenueAmount(),
                report.getAssignedEngineer().getEmail(),
                report.getTags().stream().map(ReportTag::getLabel).toList()
        );
    }
}
