package com.backend.backend.bdd;

import com.backend.backend.Enums.PeriodType;
import com.backend.backend.Enums.ReportStatus;
import com.backend.backend.entities.Engineer;
import com.backend.backend.entities.EngineerBenefit;
import com.backend.backend.entities.Report;
import com.backend.backend.metier.services.EnterpriseService;
import com.backend.backend.metier.services.KpiService;
import com.backend.backend.metier.services.ReportWorkflowService;
import com.backend.backend.repositories.ReportRepository;
import io.cucumber.java.fr.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class KpiStepDefinitions {

    @Autowired private KpiService kpiService;
    @Autowired private EnterpriseService enterpriseService;
    @Autowired private ReportWorkflowService workflowService;
    @Autowired private ReportRepository reportRepository;
    @Autowired private BddContext context;

    private EngineerBenefit lastBenefit;

    @Quand("le gérant consulte le dashboard de l'entreprise {string}")
    public void consulterDashboard(String enterpriseName) {
        var enterprise = enterpriseService.findByName(enterpriseName);
        context.setDashboardStatusCount(kpiService.countReportsInStatus(enterprise.getId(), ReportStatus.EN_COURS));
    }

    @Alors("le dashboard affiche {int} rapport en statut {string}")
    public void dashboardAffiche(int count, String status) {
        var enterprise = enterpriseService.findByName("Bati Geo Études");
        long actual = kpiService.countReportsInStatus(enterprise.getId(), ReportStatus.valueOf(status));
        assertThat(actual).isEqualTo(count);
    }

    @Et("le rapport {string} a été envoyé au client en juin 2026")
    public void rapportEnvoyeJuin(String reference) {
        marquerEnvoye(reference, LocalDateTime.of(2026, 6, 15, 10, 0));
    }

    @Et("le rapport {string} a été envoyé au client en avril 2026")
    public void rapportEnvoyeAvril(String reference) {
        marquerEnvoye(reference, LocalDateTime.of(2026, 4, 10, 10, 0));
    }

    @Et("le rapport {string} a été envoyé au client en mai 2026")
    public void rapportEnvoyeMai(String reference) {
        marquerEnvoye(reference, LocalDateTime.of(2026, 5, 10, 10, 0));
    }

    private void marquerEnvoye(String reference, LocalDateTime sentAt) {
        Report report = workflowService.findByReference(reference);
        report.setStatus(ReportStatus.ENVOYE);
        report.setSentAt(sentAt);
        context.storeReport(reference, reportRepository.save(report));
    }

    @Quand("le gérant calcule le bénéfice mensuel de juin 2026 pour {string}")
    public void calculBeneficeMensuel(String email) {
        Engineer engineer = context.getEngineer(email);
        lastBenefit = kpiService.computeBenefit(
                engineer.getEnterprise().getId(), engineer, PeriodType.MENSUEL, 2026, 6);
    }

    @Quand("le gérant calcule le bénéfice trimestriel T2 2026 pour {string}")
    public void calculBeneficeTrimestriel(String email) {
        Engineer engineer = context.getEngineer(email);
        lastBenefit = kpiService.computeBenefit(
                engineer.getEnterprise().getId(), engineer, PeriodType.TRIMESTRIEL, 2026, 2);
    }

    @Alors("le bénéfice mensuel de {string} pour juin 2026 est de {int} euros")
    public void verifierBeneficeMensuel(String email, int expected) {
        assertThat(lastBenefit.getTotalRevenue()).isEqualByComparingTo(BigDecimal.valueOf(expected));
    }

    @Alors("le bénéfice trimestriel de {string} pour T2 2026 est de {int} euros")
    public void verifierBeneficeTrimestriel(String email, int expected) {
        assertThat(lastBenefit.getTotalRevenue()).isEqualByComparingTo(BigDecimal.valueOf(expected));
    }
}
