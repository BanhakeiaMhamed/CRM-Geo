package com.backend.backend.metier.services;

import com.backend.backend.Enums.PeriodType;
import com.backend.backend.Enums.PeriodType;
import com.backend.backend.Enums.ReportStatus;
import com.backend.backend.entities.Engineer;
import com.backend.backend.entities.EngineerBenefit;
import com.backend.backend.entities.Report;
import com.backend.backend.repositories.EngineerBenefitRepository;
import com.backend.backend.repositories.IEngineerRepository;
import com.backend.backend.repositories.ReportRepository;
import com.backend.backend.repositories.TimeEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.backend.backend.dto.DashboardBenefitsDto;
import com.backend.backend.dto.EngineerBenefitDto;
import com.backend.backend.dto.InvoicedReportDto;
import com.backend.backend.dto.MonthlyRevenueDto;

@Service
@RequiredArgsConstructor
public class KpiService {

    private final EngineerBenefitRepository benefitRepository;
    private final ReportRepository reportRepository;
    private final TimeEntryRepository timeEntryRepository;
    private final IEngineerRepository engineerRepository;

    @Transactional
    public EngineerBenefit computeBenefit(Long enterpriseId, Engineer engineer,
                                          PeriodType periodType, int year, int periodNumber) {
        LocalDate start = periodStart(periodType, year, periodNumber);
        LocalDate end = periodEnd(periodType, year, periodNumber);

        List<Report> sentReports = reportRepository.findByAssignedEngineer(engineer).stream()
                .filter(r -> r.getStatus() == ReportStatus.ENVOYE || r.getStatus() == ReportStatus.CLOTURE)
                .filter(r -> r.getSentAt() != null)
                .filter(r -> !r.getSentAt().toLocalDate().isBefore(start)
                        && !r.getSentAt().toLocalDate().isAfter(end))
                .toList();

        BigDecimal totalRevenue = sentReports.stream()
                .map(r -> r.getRevenueAmount() != null ? r.getRevenueAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalHours = timeEntryRepository.sumHoursByEngineerBetween(engineer, start, end);

        EngineerBenefit benefit = benefitRepository
                .findByEngineerAndPeriodTypeAndYearAndPeriodNumber(engineer, periodType, year, periodNumber)
                .orElse(EngineerBenefit.builder()
                        .engineer(engineer)
                        .periodType(periodType)
                        .year(year)
                        .periodNumber(periodNumber)
                        .build());

        benefit.setTotalRevenue(totalRevenue);
        benefit.setTotalHours(totalHours);
        return benefitRepository.save(benefit);
    }

    public List<EngineerBenefit> dashboardForEnterprise(Long enterpriseId, PeriodType periodType, int year) {
        return benefitRepository.findByEngineerEnterpriseIdAndPeriodTypeAndYear(enterpriseId, periodType, year);
    }

    public long countReportsInStatus(Long enterpriseId, ReportStatus status) {
        return reportRepository.findByEnterpriseIdAndStatus(enterpriseId, status).size();
    }

    @Transactional(readOnly = true)
    public DashboardBenefitsDto engineerBenefitsDashboard(Long enterpriseId, int year,
                                                          String periodType, Integer periodNumber) {
        List<EngineerBenefitDto> engineers = engineerRepository.findByEnterpriseId(enterpriseId).stream()
                .map(engineer -> buildEngineerBenefit(engineer, year, periodType, periodNumber))
                .sorted(Comparator.comparingDouble(EngineerBenefitDto::totalRevenue).reversed())
                .toList();
        return new DashboardBenefitsDto(year, engineers);
    }

    private EngineerBenefitDto buildEngineerBenefit(Engineer engineer, int year,
                                                  String periodType, Integer periodNumber) {
        List<Report> completedReports = reportRepository.findByAssignedEngineer(engineer).stream()
                .filter(r -> r.getStatus() == ReportStatus.ENVOYE || r.getStatus() == ReportStatus.CLOTURE)
                .filter(r -> r.getSentAt() != null)
                .filter(r -> r.getSentAt().getYear() == year)
                .filter(r -> matchesPeriod(r, periodType, periodNumber))
                .toList();

        BigDecimal totalRevenue = completedReports.stream()
                .map(r -> r.getRevenueAmount() != null ? r.getRevenueAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);
        BigDecimal totalHours = timeEntryRepository.sumHoursByEngineerBetween(engineer, yearStart, yearEnd);
        if (totalHours == null) {
            totalHours = BigDecimal.ZERO;
        }

        Map<Integer, BigDecimal> byMonth = new LinkedHashMap<>();
        IntStream.rangeClosed(1, 12).forEach(month -> byMonth.put(month, BigDecimal.ZERO));

        for (Report report : completedReports) {
            int month = report.getSentAt().getMonthValue();
            BigDecimal amount = report.getRevenueAmount() != null ? report.getRevenueAmount() : BigDecimal.ZERO;
            byMonth.merge(month, amount, BigDecimal::add);
        }

        List<MonthlyRevenueDto> monthlyRevenue = IntStream.rangeClosed(1, 12)
                .mapToObj(month -> new MonthlyRevenueDto(month, byMonth.getOrDefault(month, BigDecimal.ZERO).doubleValue()))
                .toList();

        List<InvoicedReportDto> invoicedReports = completedReports.stream()
                .sorted(Comparator.comparing(Report::getSentAt).reversed())
                .map(r -> new InvoicedReportDto(
                        r.getReference(),
                        r.getTitle(),
                        r.getStatus(),
                        r.getRevenueAmount() != null ? r.getRevenueAmount().doubleValue() : 0,
                        r.getSentAt().toLocalDate().toString()
                ))
                .toList();

        long sentCount = completedReports.stream()
                .filter(r -> r.getStatus() == ReportStatus.ENVOYE)
                .count();
        long closedCount = completedReports.stream()
                .filter(r -> r.getStatus() == ReportStatus.CLOTURE)
                .count();
        long activeCount = reportRepository.findByAssignedEngineer(engineer).stream()
                .filter(r -> r.getStatus() != ReportStatus.ENVOYE && r.getStatus() != ReportStatus.CLOTURE)
                .count();

        return new EngineerBenefitDto(
                engineer.getEmail(),
                engineer.getFirstName(),
                engineer.getLastName(),
                totalRevenue.doubleValue(),
                totalHours.doubleValue(),
                sentCount,
                closedCount,
                activeCount,
                monthlyRevenue,
                invoicedReports
        );
    }

    private boolean matchesPeriod(Report report, String periodType, Integer periodNumber) {
        if (periodType == null || periodNumber == null) {
            return true;
        }
        int month = report.getSentAt().getMonthValue();
        if ("MENSUEL".equalsIgnoreCase(periodType)) {
            return month == periodNumber;
        }
        if ("TRIMESTRIEL".equalsIgnoreCase(periodType)) {
            int quarter = (month - 1) / 3 + 1;
            return quarter == periodNumber;
        }
        return true;
    }

    private LocalDate periodStart(PeriodType type, int year, int periodNumber) {
        if (type == PeriodType.MENSUEL) {
            return LocalDate.of(year, periodNumber, 1);
        }
        int startMonth = (periodNumber - 1) * 3 + 1;
        return LocalDate.of(year, startMonth, 1);
    }

    private LocalDate periodEnd(PeriodType type, int year, int periodNumber) {
        if (type == PeriodType.MENSUEL) {
            Month month = Month.of(periodNumber);
            return LocalDate.of(year, month, month.length(LocalDate.of(year, 1, 1).isLeapYear()));
        }
        int endMonth = periodNumber * 3;
        Month month = Month.of(endMonth);
        return LocalDate.of(year, month, month.length(LocalDate.of(year, 1, 1).isLeapYear()));
    }
}
