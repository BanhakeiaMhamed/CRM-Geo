package com.backend.backend.dto;

import com.backend.backend.Enums.ReportStatus;
import com.backend.backend.Enums.ReportType;
import com.backend.backend.entities.Report;

public record InterventionMapDto(
        String reference,
        String title,
        ReportType reportType,
        ReportStatus status,
        String address,
        String postalCode,
        String city,
        double latitude,
        double longitude,
        int pageCount,
        String engineerFirstName,
        String engineerLastName
) {
    public static InterventionMapDto from(Report report) {
        return new InterventionMapDto(
                report.getReference(),
                report.getTitle(),
                report.getReportType(),
                report.getStatus(),
                report.getAddress(),
                report.getPostalCode(),
                report.getCity(),
                report.getLatitude(),
                report.getLongitude(),
                report.getPageCount() != null ? report.getPageCount() : 0,
                report.getAssignedEngineer().getFirstName(),
                report.getAssignedEngineer().getLastName()
        );
    }
}
