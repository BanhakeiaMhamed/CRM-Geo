package com.backend.backend.metier.services;

import com.backend.backend.Enums.ClientType;
import com.backend.backend.Enums.ReportStatus;
import com.backend.backend.Enums.ReportType;
import com.backend.backend.Enums.Role;
import com.backend.backend.entities.*;
import com.backend.backend.repositories.ClientRepository;
import com.backend.backend.repositories.EnterpriseRepository;
import com.backend.backend.repositories.IEngineerRepository;
import com.backend.backend.repositories.ReportRepository;
import com.backend.backend.repositories.TimeEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemoDataService {

    public static final String DEMO_ENTERPRISE = "Bati Geo Études";

    private final EnterpriseRepository enterpriseRepository;
    private final IEngineerRepository engineerRepository;
    private final ReportRepository reportRepository;
    private final ClientRepository clientRepository;
    private final TimeEntryRepository timeEntryRepository;
    private final ReportWorkflowService reportWorkflowService;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public Enterprise ensureEnterprise() {
        return enterpriseRepository.findByName(DEMO_ENTERPRISE)
                .orElseGet(() -> enterpriseRepository.save(Enterprise.builder()
                        .name(DEMO_ENTERPRISE)
                        .siret("12345678901234")
                        .email("contact@batigeo.fr")
                        .build()));
    }

    /** Jeu de donnees complet pour tests UI (Kanban, KPI, clients, temps). */
    @Transactional
    public void seedFullDemoDataset() {
        relaxClientTableConstraints();
        Enterprise enterprise = ensureEnterprise();

        Engineer marie = ensureEngineer(enterprise, "Marie", "Dupont", "demo@batigeo.fr", "mdupont", Role.INGENIEUR);
        Engineer thomas = ensureEngineer(enterprise, "Thomas", "Leroy", "tleroy@batigeo.fr", "tleroy", Role.INGENIEUR);
        Engineer sophie = ensureEngineer(enterprise, "Sophie", "Bernard", "sbernard@batigeo.fr", "sbernard", Role.INGENIEUR);
        ensureEngineer(enterprise, "Gerard", "Martin", "gerant@batigeo.fr", "gmartin", Role.GERANT);

        seedKpiReport(marie, enterprise, "RAP-KPI-001", "Etude geotechnique Lot A",
                ReportType.ETUDE_GEOTECHNIQUE, ReportStatus.EN_COURS, "3200", null);
        seedKpiReport(marie, enterprise, "RAP-KPI-002", "Diagnostic structure batiment B",
                ReportType.DIAGNOSTIC, ReportStatus.EN_VALIDATION, "2800", null);
        seedKpiReport(marie, enterprise, "RAP-KPI-003", "Expertise fissures facade",
                ReportType.EXPERTISE, ReportStatus.EN_RECTIFICATION, "1900", null);
        seedKpiReport(marie, enterprise, "RAP-KPI-004", "Audit energetique immeuble C",
                ReportType.AUDIT_ENERGETIQUE, ReportStatus.VALIDE, "5100", null);
        seedKpiReport(marie, enterprise, "RAP-KPI-005", "Etude structure parking",
                ReportType.ETUDE_STRUCTURE, ReportStatus.ENVOYE, "5100", Month.JANUARY);
        seedKpiReport(marie, enterprise, "RAP-KPI-006", "Fondations immeuble E",
                ReportType.ETUDE_GEOTECHNIQUE, ReportStatus.CLOTURE, "7200", Month.MARCH);

        seedKpiReport(thomas, enterprise, "RAP-KPI-007", "Controle portique metallique",
                ReportType.ETUDE_STRUCTURE, ReportStatus.EN_COURS, "4100", null);
        seedKpiReport(thomas, enterprise, "RAP-KPI-008", "Rapport sols zone industrielle",
                ReportType.ETUDE_GEOTECHNIQUE, ReportStatus.EN_VALIDATION, "3600", null);
        seedKpiReport(thomas, enterprise, "RAP-KPI-009", "Diagnostic charpente hangar",
                ReportType.DIAGNOSTIC, ReportStatus.ENVOYE, "5400", Month.FEBRUARY);
        seedKpiReport(thomas, enterprise, "RAP-KPI-010", "Expertise infiltrations",
                ReportType.EXPERTISE, ReportStatus.CLOTURE, "4800", Month.APRIL);

        seedKpiReport(sophie, enterprise, "RAP-KPI-011", "Etude fondations villa",
                ReportType.ETUDE_GEOTECHNIQUE, ReportStatus.VALIDE, "2900", null);
        seedKpiReport(sophie, enterprise, "RAP-KPI-012", "Audit thermique immeuble D",
                ReportType.AUDIT_ENERGETIQUE, ReportStatus.ENVOYE, "6200", Month.MAY);
        seedKpiReport(sophie, enterprise, "RAP-KPI-013", "Rapport stabilite talus",
                ReportType.ETUDE_GEOTECHNIQUE, ReportStatus.CLOTURE, "3900", Month.JUNE);

        seedDemoClients(marie, thomas);
        seedDemoTimeEntries(marie, "RAP-KPI-001", "RAP-KPI-005");
        seedDemoTimeEntries(thomas, "RAP-KPI-007", "RAP-KPI-009");
        seedDemoTimeEntries(sophie, "RAP-KPI-011", "RAP-KPI-012");

        engineerRepository.findByEnterpriseId(enterprise.getId())
                .forEach(this::fillMissingTimeEntries);

        log.info("Donnees de demo injectees pour {}.", DEMO_ENTERPRISE);
    }

    /** Donnees minimales pour un nouvel utilisateur Firebase (Kanban + temps + clients). */
    @Transactional
    public void seedUserDemoData(Engineer engineer, Enterprise enterprise) {
        if (reportRepository.findByAssignedEngineer(engineer).isEmpty()) {
            seedKpiReport(engineer, enterprise, "RAP-USER-001", "Etude geotechnique Lot A",
                    ReportType.ETUDE_GEOTECHNIQUE, ReportStatus.EN_COURS, "4500", null);
            seedKpiReport(engineer, enterprise, "RAP-USER-002", "Diagnostic structure batiment B",
                    ReportType.DIAGNOSTIC, ReportStatus.EN_VALIDATION, "2800", null);
            seedKpiReport(engineer, enterprise, "RAP-USER-003", "Expertise fissures facade",
                    ReportType.EXPERTISE, ReportStatus.EN_RECTIFICATION, "1900", null);
            seedKpiReport(engineer, enterprise, "RAP-USER-004", "Audit energetique immeuble C",
                    ReportType.AUDIT_ENERGETIQUE, ReportStatus.VALIDE, "5100", null);
            seedKpiReport(engineer, enterprise, "RAP-USER-005", "Etude structure parking",
                    ReportType.ETUDE_STRUCTURE, ReportStatus.ENVOYE, "7200", Month.JANUARY);
            seedKpiReport(engineer, enterprise, "RAP-USER-006", "Controle fondations parking P2",
                    ReportType.ETUDE_GEOTECHNIQUE, ReportStatus.CLOTURE, "3400", Month.FEBRUARY);
        }
        if (clientRepository.findByEnterpriseId(enterprise.getId()).isEmpty()) {
            seedDemoClients(engineer, engineer);
        }
        if (timeEntryRepository.findByEngineer(engineer).isEmpty()) {
            fillMissingTimeEntries(engineer);
        }
    }

    private void fillMissingTimeEntries(Engineer engineer) {
        var reports = reportRepository.findByAssignedEngineer(engineer);
        if (reports.isEmpty()) {
            return;
        }
        String ref1 = reports.get(0).getReference();
        String ref2 = reports.size() > 1 ? reports.get(1).getReference() : ref1;
        seedDemoTimeEntries(engineer, ref1, ref2);
    }

    private Engineer ensureEngineer(Enterprise enterprise, String firstName, String lastName,
                                    String email, String userName, Role role) {
        return engineerRepository.findByEmail(email)
                .orElseGet(() -> engineerRepository.save(Engineer.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .userName(userName)
                        .password("FIREBASE_AUTH")
                        .role(role)
                        .enterprise(enterprise)
                        .build()));
    }

    private void seedKpiReport(Engineer engineer, Enterprise enterprise, String reference, String title,
                               ReportType type, ReportStatus status, String revenue, Month sentMonth) {
        if (reportRepository.findByReference(reference).isPresent()) {
            return;
        }
        Report report = reportWorkflowService.createReport(
                title, reference, type, enterprise, engineer, new BigDecimal(revenue));
        report.setStatus(status);
        if (sentMonth != null) {
            report.setSentAt(LocalDateTime.of(2026, sentMonth, 15, 10, 0));
        }
        reportRepository.save(report);
    }

    private void seedDemoClients(Engineer owner1, Engineer owner2) {
        ensureParticulier(owner1, "Jean", "Moreau", "jean.moreau@email.fr", "0612345678");
        ensureParticulier(owner2, "Claire", "Petit", "claire.petit@email.fr", "0698765432");
        ensureProfesionnel(owner1, "Promoteur Lyon Sud", "123456789", "12345678901234", "SAS",
                "contact@promoteur-lyon.fr", "0478000000");
        ensureProfesionnel(owner2, "Batiment Confluence", "987654321", "98765432109876", "SARL",
                "info@bat-confluence.fr", "0478111111");
    }

    private void ensureParticulier(Engineer engineer, String firstName, String lastName,
                                   String email, String phone) {
        if (clientRepository.findByEnterpriseId(engineer.getEnterprise().getId()).stream()
                .anyMatch(c -> email.equals(c.getEmail()))) {
            return;
        }
        Particulier client = new Particulier();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        client.setPhone(phone);
        client.setClientType(ClientType.PARTICULIER);
        client.setEngineer(engineer);
        clientRepository.save(client);
    }

    private void ensureProfesionnel(Engineer engineer, String companyName, String siren, String siret,
                                    String companyType, String email, String phone) {
        if (clientRepository.findByEnterpriseId(engineer.getEnterprise().getId()).stream()
                .anyMatch(c -> email.equals(c.getEmail()))) {
            return;
        }
        Profesionnel client = new Profesionnel();
        client.setCompanyName(companyName);
        client.setSiren(siren);
        client.setSiret(siret);
        client.setCompanyType(companyType);
        client.setEmail(email);
        client.setPhone(phone);
        client.setClientType(ClientType.PROFESSIONAL);
        client.setEngineer(engineer);
        clientRepository.save(client);
    }

    private void seedDemoTimeEntries(Engineer engineer, String refActive, String refSent) {
        reportRepository.findByReference(refActive).ifPresent(report -> {
            if (timeEntryRepository.findByEngineer(engineer).stream()
                    .noneMatch(e -> e.getReport().getReference().equals(refActive))) {
                saveTimeEntry(report, engineer, LocalDate.now().minusDays(2), "4.00", "Relevé terrain");
                saveTimeEntry(report, engineer, LocalDate.now().minusDays(1), "3.50", "Analyse labo");
            }
        });
        reportRepository.findByReference(refSent).ifPresent(report -> {
            if (timeEntryRepository.findByEngineer(engineer).stream()
                    .noneMatch(e -> e.getReport().getReference().equals(refSent))) {
                saveTimeEntry(report, engineer, LocalDate.of(2026, 1, 10), "6.00", "Redaction rapport");
                saveTimeEntry(report, engineer, LocalDate.of(2026, 1, 12), "2.00", "Relecture");
            }
        });
    }

    private void saveTimeEntry(Report report, Engineer engineer, LocalDate date,
                               String hours, String comment) {
        timeEntryRepository.save(TimeEntry.builder()
                .report(report)
                .engineer(engineer)
                .entryDate(date)
                .hoursSpent(new BigDecimal(hours))
                .comment(comment)
                .build());
    }

    /** Assouplit le schema clients (single-table) pour les jeux de donnees de test. */
    private void relaxClientTableConstraints() {
        for (String column : List.of("company_name", "siren", "siret", "company_type",
                "first_name", "last_name", "client_kind")) {
            try {
                jdbcTemplate.execute("ALTER TABLE clients ALTER COLUMN " + column + " DROP NOT NULL");
            } catch (Exception ignored) {
                // deja nullable ou table absente
            }
        }
    }
}
