package com.backend.backend.bdd;

import com.backend.backend.Enums.ReportStatus;
import com.backend.backend.entities.Engineer;
import com.backend.backend.entities.Report;
import com.backend.backend.metier.services.EnterpriseService;
import com.backend.backend.metier.services.ReportWorkflowService;
import org.springframework.transaction.annotation.Transactional;
import io.cucumber.java.fr.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class KanbanStepDefinitions {

    @Autowired private ReportWorkflowService workflowService;
    @Autowired private EnterpriseService enterpriseService;
    @Autowired private BddContext context;

    @Quand("l'ingénieur {string} consulte son tableau Kanban")
    public void consulterKanban(String email) {
        Engineer engineer = context.getEngineer(email);
        if (engineer == null) {
            engineer = enterpriseService.findEngineerByEmail(email);
        }
        List<Report> board = workflowService.findKanbanBoard(engineer);
        context.setKanbanCounts(board.stream()
                .collect(Collectors.groupingBy(r -> r.getStatus().name(), Collectors.counting())));
    }

    @Alors("la colonne {string} contient {int} rapport")
    public void colonneContient(String status, int count) {
        assertThat(context.getKanbanCounts().getOrDefault(status, 0L)).isEqualTo(count);
    }

    @Quand("l'ingénieur ajoute le tag {string} au rapport {string}")
    public void ajouterTag(String tag, String reference) {
        Report report = workflowService.addTag(reference, tag);
        context.storeReport(reference, report);
    }

    @Alors("le rapport {string} possède les tags {string} et {string}")
    @Transactional
    public void verifierTags(String reference, String tag1, String tag2) {
        Report report = workflowService.findByReference(reference);
        assertThat(report.getTags()).extracting(t -> t.getLabel())
                .containsExactlyInAnyOrder(tag1, tag2);
    }
}
