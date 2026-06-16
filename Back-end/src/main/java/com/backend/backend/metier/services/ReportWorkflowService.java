package com.backend.backend.metier.services;

import com.backend.backend.Enums.ReportStatus;
import com.backend.backend.Enums.ReportType;
import com.backend.backend.dto.ReportDto;
import com.backend.backend.entities.*;
import com.backend.backend.exceptions.InvalidWorkflowTransitionException;
import com.backend.backend.exceptions.ReportNotFoundException;
import com.backend.backend.repositories.ReportRepository;
import com.backend.backend.repositories.ReportTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReportWorkflowService {

    private static final Map<ReportStatus, Set<ReportStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(ReportStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(ReportStatus.BROUILLON, EnumSet.of(ReportStatus.EN_COURS));
        ALLOWED_TRANSITIONS.put(ReportStatus.EN_COURS, EnumSet.of(ReportStatus.EN_VALIDATION));
        ALLOWED_TRANSITIONS.put(ReportStatus.EN_VALIDATION, EnumSet.of(ReportStatus.VALIDE, ReportStatus.EN_RECTIFICATION));
        ALLOWED_TRANSITIONS.put(ReportStatus.EN_RECTIFICATION, EnumSet.of(ReportStatus.EN_COURS));
        ALLOWED_TRANSITIONS.put(ReportStatus.VALIDE, EnumSet.of(ReportStatus.ENVOYE));
        ALLOWED_TRANSITIONS.put(ReportStatus.ENVOYE, EnumSet.of(ReportStatus.CLOTURE));
    }

    private final ReportRepository reportRepository;
    private final ReportTagRepository reportTagRepository;

    @Transactional
    public Report createReport(String title, String reference, ReportType type,
                               Enterprise enterprise, Engineer engineer, BigDecimal revenueAmount) {
        Report report = Report.builder()
                .title(title)
                .reference(reference)
                .reportType(type)
                .status(ReportStatus.EN_COURS)
                .enterprise(enterprise)
                .assignedEngineer(engineer)
                .revenueAmount(revenueAmount)
                .build();
        return reportRepository.save(report);
    }

    @Transactional
    public Report addTag(String reference, String tagLabel) {
        Report report = findByReference(reference);
        ReportTag tag = reportTagRepository
                .findByEnterpriseIdAndLabel(report.getEnterprise().getId(), tagLabel)
                .orElseGet(() -> reportTagRepository.save(ReportTag.builder()
                        .label(tagLabel)
                        .color("#1976D2")
                        .enterprise(report.getEnterprise())
                        .build()));
        report.getTags().add(tag);
        return reportRepository.save(report);
    }

    @Transactional
    public Report transition(String reference, ReportStatus targetStatus) {
        Report report = findByReference(reference);
        ReportStatus current = report.getStatus();

        if (!ALLOWED_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(ReportStatus.class)).contains(targetStatus)) {
            throw new InvalidWorkflowTransitionException(
                    "Transition impossible : " + current + " → " + targetStatus);
        }

        report.setStatus(targetStatus);
        LocalDateTime now = LocalDateTime.now();
        switch (targetStatus) {
            case EN_VALIDATION -> report.setSubmittedAt(now);
            case VALIDE -> report.setValidatedAt(now);
            case ENVOYE -> report.setSentAt(now);
            default -> { }
        }
        return reportRepository.save(report);
    }

    public Report findByReference(String reference) {
        return reportRepository.findByReference(reference)
                .orElseThrow(() -> new ReportNotFoundException(reference));
    }

    public List<Report> findByEngineerAndStatus(Engineer engineer, ReportStatus status) {
        return reportRepository.findByAssignedEngineerAndStatus(engineer, status);
    }

    public List<Report> findKanbanBoard(Engineer engineer) {
        return reportRepository.findByAssignedEngineer(engineer);
    }

    @Transactional(readOnly = true)
    public List<ReportDto> findKanbanBoardDto(Engineer engineer) {
        return findKanbanBoard(engineer).stream().map(ReportDto::from).toList();
    }
}
