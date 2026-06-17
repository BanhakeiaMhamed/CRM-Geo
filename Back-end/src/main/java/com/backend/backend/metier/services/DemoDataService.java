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
    private final GeoCodingService geoCodingService;
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

        seedKpiReport(marie, enterprise, "RAP-KPI-001", "G1 ES — Lotissement Les Oliviers",
                ReportType.G1_ES, ReportStatus.EN_COURS, "3200", null,
                "45 avenue Jean Jaures", "69007", "Lyon", 7);
        seedKpiReport(marie, enterprise, "RAP-KPI-002", "G1 PGC — Immeuble de bureaux B",
                ReportType.G1_PGC, ReportStatus.EN_VALIDATION, "2800", null,
                "12 rue de la Charite", "69002", "Lyon", 14);
        seedKpiReport(marie, enterprise, "RAP-KPI-003", "G2 AVP — Extension usine Nord",
                ReportType.G2_AVP, ReportStatus.EN_RECTIFICATION, "1900", null,
                "8 rue Garibaldi", "69006", "Lyon", 11);
        seedKpiReport(marie, enterprise, "RAP-KPI-004", "G2 PRO — Parking souterrain C",
                ReportType.G2_PRO, ReportStatus.VALIDE, "5100", null,
                "22 cours Lafayette", "69003", "Lyon", 18);
        seedKpiReport(marie, enterprise, "RAP-KPI-005", "G2 DCE/ACT — Voirie secteur Est",
                ReportType.G2_DCE_ACT, ReportStatus.ENVOYE, "5100", Month.JANUARY,
                "5 place Bellecour", "69002", "Lyon", 22);
        seedKpiReport(marie, enterprise, "RAP-KPI-006", "G3 Phase Etude — Fondations immeuble E",
                ReportType.G3_PHASE_ETUDE, ReportStatus.CLOTURE, "7200", Month.MARCH,
                "18 rue Paul Bert", "69100", "Villeurbanne", 26);

        seedKpiReport(thomas, enterprise, "RAP-KPI-007", "G3 Phase Suivi — Portique metallique",
                ReportType.G3_PHASE_SUIVI, ReportStatus.EN_COURS, "4100", null,
                "Zone industrielle Nord, bat. C", "69100", "Villeurbanne", 5);
        seedKpiReport(thomas, enterprise, "RAP-KPI-008", "G1 ES — Zone industrielle Sud",
                ReportType.G1_ES, ReportStatus.EN_VALIDATION, "3600", null,
                "140 avenue Henri Barbusse", "69100", "Villeurbanne", 15);
        seedKpiReport(thomas, enterprise, "RAP-KPI-009", "G4 Supervision etude — Hangar logistique",
                ReportType.G4_SUPERVISION_ETUDE, ReportStatus.ENVOYE, "5400", Month.FEBRUARY,
                "Chemin de la Mouche", "69200", "Venissieux", 20);
        seedKpiReport(thomas, enterprise, "RAP-KPI-010", "G4 Supervision suivi — Ouvrage de stockage",
                ReportType.G4_SUPERVISION_SUIVI, ReportStatus.CLOTURE, "4800", Month.APRIL,
                "25 rue Marcel Merieux", "69200", "Venissieux", 24);

        seedKpiReport(sophie, enterprise, "RAP-KPI-011", "G2 PRO — Fondations villa",
                ReportType.G2_PRO, ReportStatus.VALIDE, "2900", null,
                "7 impasse des Lilas", "69300", "Caluire-et-Cuire", 16);
        seedKpiReport(sophie, enterprise, "RAP-KPI-012", "G3 Phase Suivi — Immeuble D",
                ReportType.G3_PHASE_SUIVI, ReportStatus.ENVOYE, "6200", Month.MAY,
                "3 montee de la Grande Cote", "69001", "Lyon", 19);
        seedKpiReport(sophie, enterprise, "RAP-KPI-013", "G5 Diagnostic — Stabilite talus",
                ReportType.G5_DIAGNOSTIC, ReportStatus.CLOTURE, "3900", Month.JUNE,
                "Route de Vourles", "69600", "Oullins", 21);

        seedDemoClients(marie, thomas);
        seedDemoTimeEntries(marie, "RAP-KPI-001", "RAP-KPI-005");
        seedDemoTimeEntries(thomas, "RAP-KPI-007", "RAP-KPI-009");
        seedDemoTimeEntries(sophie, "RAP-KPI-011", "RAP-KPI-012");

        engineerRepository.findByEnterpriseId(enterprise.getId())
                .forEach(this::fillMissingTimeEntries);

        refreshOrphanReportLocations(enterprise);

        log.info("Donnees de demo injectees pour {}.", DEMO_ENTERPRISE);
    }

    /** Donnees minimales pour un nouvel utilisateur Firebase (Kanban + temps + clients). */
    @Transactional
    public void seedUserDemoData(Engineer engineer, Enterprise enterprise) {
        if (reportRepository.findByAssignedEngineer(engineer).isEmpty()) {
            seedKpiReport(engineer, enterprise, "RAP-USER-001", "G1 ES — Etude de site Lot A",
                    ReportType.G1_ES, ReportStatus.EN_COURS, "4500", null,
                    "10 rue Victor Hugo", "69002", "Lyon", 6);
            seedKpiReport(engineer, enterprise, "RAP-USER-002", "G1 PGC — Batiment B",
                    ReportType.G1_PGC, ReportStatus.EN_VALIDATION, "2800", null,
                    "34 rue de la Part-Dieu", "69003", "Lyon", 13);
            seedKpiReport(engineer, enterprise, "RAP-USER-003", "G2 AVP — Fissures facade",
                    ReportType.G2_AVP, ReportStatus.EN_RECTIFICATION, "1900", null,
                    "6 rue Moncey", "69003", "Lyon", 12);
            seedKpiReport(engineer, enterprise, "RAP-USER-004", "G2 PRO — Immeuble C",
                    ReportType.G2_PRO, ReportStatus.VALIDE, "5100", null,
                    "15 quai Rambaud", "69002", "Lyon", 17);
            seedKpiReport(engineer, enterprise, "RAP-USER-005", "G3 Phase Etude — Parking",
                    ReportType.G3_PHASE_ETUDE, ReportStatus.ENVOYE, "7200", Month.JANUARY,
                    "2 place des Jacobins", "69002", "Lyon", 23);
            seedKpiReport(engineer, enterprise, "RAP-USER-006", "G5 Diagnostic — Fondations P2",
                    ReportType.G5_DIAGNOSTIC, ReportStatus.CLOTURE, "3400", Month.FEBRUARY,
                    "88 avenue Jean Jaures", "69100", "Villeurbanne", 25);
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

    private static final String DEFAULT_MIGRATION_ADDRESS = "10 rue de la Republique";

    private void seedKpiReport(Engineer engineer, Enterprise enterprise, String reference, String title,
                               ReportType type, ReportStatus status, String revenue, Month sentMonth,
                               String address, String postalCode, String city, int pageCount) {
        reportRepository.findByReference(reference).ifPresentOrElse(existing -> {
            applyLocation(existing, address, postalCode, city);
            existing.setPageCount(pageCount);
            if (sentMonth != null) {
                existing.setSentAt(LocalDateTime.of(2026, sentMonth, 15, 10, 0));
            }
            reportRepository.save(existing);
        }, () -> {
            Report report = reportWorkflowService.createReport(
                    title, reference, type, enterprise, engineer, new BigDecimal(revenue),
                    address, postalCode, city);
            report.setStatus(status);
            report.setPageCount(pageCount);
            if (sentMonth != null) {
                report.setSentAt(LocalDateTime.of(2026, sentMonth, 15, 10, 0));
            }
            reportRepository.save(report);
        });
    }

    /** Reassigne les rapports encore sur l'adresse par defaut de migration. */
    private void refreshOrphanReportLocations(Enterprise enterprise) {
        List<String[]> locations = List.of(
                new String[]{"14 rue Auguste Comte", "69002", "Lyon"},
                new String[]{"27 rue de la Thibaudiere", "69007", "Lyon"},
                new String[]{"9 rue Dugas-Montbel", "69005", "Lyon"},
                new String[]{"41 avenue Berthelot", "69007", "Lyon"},
                new String[]{"3 rue de Bonnel", "69003", "Lyon"},
                new String[]{"56 rue Garibaldi", "69006", "Lyon"},
                new String[]{"11 rue Anatole France", "69100", "Villeurbanne"},
                new String[]{"67 rue Marceau", "28700", "Auneau"},
                new String[]{"120 boulevard Pinel", "69008", "Lyon"},
                new String[]{"4 rue de la Part-Dieu", "69003", "Lyon"},
                new String[]{"19 rue de la Republique", "69200", "Venissieux"},
                new String[]{"6 chemin de la Ferme", "69300", "Caluire-et-Cuire"},
                new String[]{"2 place Antonin Poncet", "69002", "Lyon"},
                new String[]{"31 rue du Dauphine", "69003", "Lyon"},
                new String[]{"8 rue de Cuire", "69004", "Lyon"}
        );

        var orphans = reportRepository.findByEnterpriseId(enterprise.getId()).stream()
                .filter(r -> r.getAddress() == null
                        || r.getAddress().isBlank()
                        || r.getAddress().startsWith(DEFAULT_MIGRATION_ADDRESS))
                .toList();

        for (int i = 0; i < orphans.size(); i++) {
            String[] loc = locations.get(i % locations.size());
            applyLocation(orphans.get(i), loc[0], loc[1], loc[2]);
            reportRepository.save(orphans.get(i));
        }
    }

    private void applyLocation(Report report, String address, String postalCode, String city) {
        double[] coords = geoCodingService.resolveCoordinates(address, postalCode, city);
        report.setAddress(address);
        report.setPostalCode(postalCode);
        report.setCity(city);
        report.setLatitude(coords[0]);
        report.setLongitude(coords[1]);
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
