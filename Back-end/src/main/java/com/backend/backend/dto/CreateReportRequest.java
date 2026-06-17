package com.backend.backend.dto;

import com.backend.backend.Enums.ReportType;

import java.math.BigDecimal;

public record CreateReportRequest(
        String title,
        String reference,
        ReportType reportType,
        BigDecimal revenueAmount,
        String address,
        String postalCode,
        String city
) {}
