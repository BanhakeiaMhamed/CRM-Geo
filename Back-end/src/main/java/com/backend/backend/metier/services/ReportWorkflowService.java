package com.backend.backend.metier.services;

import com.backend.backend.Enums.ReportStatus;
import com.backend.backend.Enums.ReportType;
import com.backend.backend.dto.InterventionMapDto;
import com.backend.backend.dto.ReportDto;
import com.backend.backend.dto.ReportPdfDownload;
import com.backend.backend.entities.*;
import com.backend.backend.exceptions.InvalidWorkflowTransitionException;
import com.backend.backend.exceptions.ReportNotFoundException;
import com.backend.backend.repositories.ReportRepository;
import com.backend.backend.repositories.ReportTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    public static final int MIN_PAGES_FOR_VALIDATION = 10;

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
    private final GeoCodingService geoCodingService;
    private final ReportPdfStorageService reportPdfStorageService;

    @Transactional
    public Report createReport(String title, String reference, ReportType type,
                               Enterprise enterprise, Engineer engineer, BigDecimal revenueAmount,
                               String address, String postalCode, String city) {
        validateAddress(address, postalCode, city);
        double[] coords = geoCodingService.resolveCoordinates(address, postalCode, city);
        Report report = Report.builder()
                .title(title)
                .reference(reference)
                .reportType(type)
                .status(ReportStatus.EN_COURS)
                .enterprise(enterprise)
                .assignedEngineer(engineer)
                .revenueAmount(revenueAmount)
                .address(address.trim())
                .postalCode(postalCode.trim())
                .city(city.trim())
                .latitude(coords[0])
                .longitude(coords[1])
                .pageCount(0)
                .build();
        return reportRepository.save(report);
    }

    @Transactional
    public Report updatePageCount(String reference, int pageCount) {
        if (pageCount < 0) {
            throw new IllegalArgumentException("Le nombre de pages ne peut pas etre negatif.");
        }
        Report report = findByReference(reference);
        report.setPageCount(pageCount);
        return reportRepository.save(report);
    }

    @Transactional
    public Report attachPdf(String reference, MultipartFile file) {
        Report report = findByReference(reference);
        try {
            if (report.getPdfStoredName() != null) {
                reportPdfStorageService.deleteIfExists(report.getPdfStoredName());
            }
            String storedName = reportPdfStorageService.store(reference, file);
            report.setPdfStoredName(storedName);
            report.setPdfFileName(file.getOriginalFilename());
            return reportRepository.save(report);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Impossible d'enregistrer le PDF.");
        }
    }

    @Transactional(readOnly = true)
    public ReportPdfDownload loadPdf(String reference) {
        Report report = findByReference(reference);
        if (report.getPdfStoredName() == null || report.getPdfStoredName().isBlank()) {
            throw new IllegalArgumentException("Aucun PDF joint a ce rapport.");
        }
        try {
            String fileName = report.getPdfFileName() != null ? report.getPdfFileName() : report.getReference() + ".pdf";
            return new ReportPdfDownload(fileName, reportPdfStorageService.load(report.getPdfStoredName()));
        } catch (IOException ex) {
            throw new IllegalArgumentException("Fichier PDF introuvable.");
        }
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

        if (targetStatus == ReportStatus.EN_VALIDATION) {
            validateAddress(report.getAddress(), report.getPostalCode(), report.getCity());
            int pages = report.getPageCount() != null ? report.getPageCount() : 0;
            if (pages < MIN_PAGES_FOR_VALIDATION) {
                throw new InvalidWorkflowTransitionException(
                        "Minimum " + MIN_PAGES_FOR_VALIDATION + " pages requis pour soumettre en validation (actuel : " + pages + ").");
            }
            if (report.getPdfStoredName() == null || report.getPdfStoredName().isBlank()) {
                throw new InvalidWorkflowTransitionException(
                        "Un fichier PDF doit etre joint au rapport avant validation.");
            }
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

    @Transactional(readOnly = true)
    public List<InterventionMapDto> interventionsMap(Long enterpriseId) {
        return reportRepository.findByEnterpriseId(enterpriseId).stream()
                .filter(r -> r.getLatitude() != null && r.getLongitude() != null)
                .filter(r -> r.getAddress() != null && !r.getAddress().isBlank())
                .map(InterventionMapDto::from)
                .toList();
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

    private void validateAddress(String address, String postalCode, String city) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("L'adresse est obligatoire.");
        }
        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("Le code postal est obligatoire.");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("La ville est obligatoire.");
        }
    }
}
