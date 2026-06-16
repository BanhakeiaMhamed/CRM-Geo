package com.backend.backend.dto;

import com.backend.backend.Enums.ReportStatus;

public record InvoicedReportDto(
        String reference,
        String title,
        ReportStatus status,
        double revenueAmount,
        String sentAt
) {}
