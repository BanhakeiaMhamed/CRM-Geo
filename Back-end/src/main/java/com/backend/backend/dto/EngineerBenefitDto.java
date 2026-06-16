package com.backend.backend.dto;

import java.util.List;

public record EngineerBenefitDto(
        String email,
        String firstName,
        String lastName,
        double totalRevenue,
        double totalHours,
        long sentReportsCount,
        long closedReportsCount,
        long activeReportsCount,
        List<MonthlyRevenueDto> monthlyRevenue,
        List<InvoicedReportDto> invoicedReports
) {}
