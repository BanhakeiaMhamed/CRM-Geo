package com.backend.backend.bdd;

import com.backend.backend.Enums.ReportStatus;
import com.backend.backend.Enums.ReportType;
import com.backend.backend.entities.Engineer;
import com.backend.backend.entities.Report;
import com.backend.backend.exceptions.InvalidWorkflowTransitionException;
import com.backend.backend.metier.services.EnterpriseService;
import com.backend.backend.metier.services.ReportWorkflowService;
import com.backend.backend.repositories.ReportRepository;
import io.cucumber.java.fr.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class RapportWorkflowStepDefinitions {

    @Autowired private ReportWorkflowService workflowService;
    @Autowired private EnterpriseService enterpriseService;
    @Autowired private ReportRepository reportRepository;
    @Autowired private BddContext context;

    @Etantdonné("un rapport {string} de type {string} assigné à {string}")
    public void creerRapport(String reference, String type, String email) {
        creerRapportAvecStatut(reference, type, "EN_COURS", email, null);
    }

    @Etantdonné("un rapport {string} de type {string} en statut {string} assigné à {string}")
    public void creerRapportAvecStatut(String reference, String type, String status, String email) {
        creerRapportAvecStatut(reference, type, status, email, null);
    }

    @Etantdonné("un rapport {string} de type {string} avec un bénéfice de {int} euros assigné à {string}")
    public void creerRapportAvecBenefice(String reference, String type, int revenue, String email) {
        creerRapportAvecStatut(reference, type, "EN_COURS", email, BigDecimal.valueOf(revenue));
    }

    private void creerRapportAvecStatut(String reference, String type, String status,
                                        String email, BigDecimal revenue) {
        Engineer engineer = context.getEngineer(email);
        if (engineer == null) {
            engineer = enterpriseService.findEngineerByEmail(email);
        }
        Report report = workflowService.createReport(
                "Rapport " + reference,
                reference,
                ReportType.valueOf(type),
                engineer.getEnterprise(),
                engineer,
                revenue != null ? revenue : BigDecimal.ZERO,
                "10 rue de la Republique",
                "69002",
                "Lyon"
        );
        report.setPageCount(ReportWorkflowService.MIN_PAGES_FOR_VALIDATION);
        if (!ReportStatus.EN_COURS.name().equals(status)) {
            report.setStatus(ReportStatus.valueOf(status));
            report = reportRepository.save(report);
        }
        context.storeReport(reference, report);
    }

    @Etantdonné("que le rapport {string} est en statut {string}")
    @Etantdonné("le rapport {string} est en statut {string}")
    public void rapportEnStatut(String reference, String status) {
        Report report = workflowService.findByReference(reference);
        report.setStatus(ReportStatus.valueOf(status));
        context.storeReport(reference, reportRepository.save(report));
    }

    @Quand("l'ingénieur soumet le rapport {string} pour validation")
    public void soumettrePourValidation(String reference) {
        try {
            context.setLastException(null);
            Report report = workflowService.transition(reference, ReportStatus.EN_VALIDATION);
            context.storeReport(reference, report);
        } catch (Exception e) {
            context.setLastException(e);
        }
    }

    @Quand("le gérant valide le rapport {string}")
    public void valider(String reference) {
        try {
            context.setLastException(null);
            Report report = workflowService.transition(reference, ReportStatus.VALIDE);
            context.storeReport(reference, report);
        } catch (Exception e) {
            context.setLastException(e);
        }
    }

    @Quand("le gérant envoie le rapport {string} au client")
    public void envoyer(String reference) {
        try {
            context.setLastException(null);
            Report report = workflowService.transition(reference, ReportStatus.ENVOYE);
            context.storeReport(reference, report);
        } catch (Exception e) {
            context.setLastException(e);
        }
    }

    @Quand("le gérant demande une rectification sur le rapport {string}")
    public void rectifier(String reference) {
        try {
            context.setLastException(null);
            Report report = workflowService.transition(reference, ReportStatus.EN_RECTIFICATION);
            context.storeReport(reference, report);
        } catch (Exception e) {
            context.setLastException(e);
        }
    }

    @Quand("l'ingénieur reprend le rapport {string} après rectification")
    public void reprendreApresRectification(String reference) {
        try {
            context.setLastException(null);
            Report report = workflowService.transition(reference, ReportStatus.EN_COURS);
            context.storeReport(reference, report);
        } catch (Exception e) {
            context.setLastException(e);
        }
    }

    @Quand("le gérant tente d'envoyer directement le rapport {string} au client")
    public void envoiDirect(String reference) {
        envoyer(reference);
    }

    @Alors("le rapport {string} passe en statut {string}")
    public void verifierStatut(String reference, String status) {
        Report report = workflowService.findByReference(reference);
        assertThat(report.getStatus()).isEqualTo(ReportStatus.valueOf(status));
    }

    @Alors("une erreur de transition de workflow est retournée")
    public void erreurTransition() {
        assertThat(context.getLastException()).isInstanceOf(InvalidWorkflowTransitionException.class);
    }
}
