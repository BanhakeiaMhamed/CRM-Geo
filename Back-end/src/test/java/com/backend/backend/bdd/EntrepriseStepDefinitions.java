package com.backend.backend.bdd;

import com.backend.backend.entities.Enterprise;
import com.backend.backend.entities.Engineer;
import com.backend.backend.metier.services.EnterpriseService;
import com.backend.backend.repositories.EnterpriseRepository;
import com.backend.backend.repositories.ReportRepository;
import io.cucumber.java.Before;
import io.cucumber.java.fr.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class EntrepriseStepDefinitions {

    @Autowired private EnterpriseService enterpriseService;
    @Autowired private EnterpriseRepository enterpriseRepository;
    @Autowired private ReportRepository reportRepository;
    @Autowired private com.backend.backend.repositories.TimeEntryRepository timeEntryRepository;
    @Autowired private com.backend.backend.repositories.IEngineerRepository engineerRepository;
    @Autowired private com.backend.backend.repositories.EngineerBenefitRepository engineerBenefitRepository;
    @Autowired private com.backend.backend.repositories.ReportTagRepository reportTagRepository;
    @Autowired private BddContext context;

    @Before("@entreprise or @rapport or @kpi or @temps")
    public void resetContext() {
        context.reset();
        engineerBenefitRepository.deleteAll();
        timeEntryRepository.deleteAll();
        reportRepository.deleteAll();
        reportTagRepository.deleteAll();
        engineerRepository.deleteAll();
        enterpriseRepository.deleteAll();
    }

    @Etantdonné("qu'aucune entreprise n'existe dans le système")
    @Etantdonné("aucune entreprise n'existe dans le système")
    public void aucuneEntreprise() {
        enterpriseRepository.deleteAll();
    }

    @Quand("je crée l'entreprise {string} avec le SIRET {string}")
    public void creerEntreprise(String name, String siret) {
        Enterprise enterprise = enterpriseService.createEnterprise(name, siret, name.toLowerCase().replace(" ", "") + "@crm.fr");
        context.storeEnterprise(name, enterprise);
    }

    @Et("j'y rattache l'ingénieur {string} avec l'email {string}")
    public void rattacherIngenieur(String fullName, String email) {
        Enterprise enterprise = context.getEnterprise("Bati Geo Études");
        if (enterprise == null) {
            enterprise = enterpriseRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("Aucune entreprise disponible"));
        }
        String[] parts = fullName.split(" ", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";
        Engineer engineer = enterpriseService.attachEngineer(enterprise, firstName, lastName, email, email.split("@")[0]);
        context.storeEngineer(email, engineer);
    }

    @Etantdonné("l'entreprise {string} avec le SIRET {string}")
    public void entrepriseExistante(String name, String siret) {
        Enterprise enterprise = enterpriseService.createEnterprise(name, siret, name.toLowerCase().replace(" ", "") + "@crm.fr");
        context.storeEnterprise(name, enterprise);
    }

    @Et("l'ingénieur {string} rattaché avec l'email {string}")
    public void ingenierRattache(String fullName, String email) {
        rattacherIngenieur(fullName, email);
    }

    @Alors("l'entreprise {string} existe dans le système")
    public void entrepriseExiste(String name) {
        assertThat(enterpriseRepository.findByName(name)).isPresent();
    }

    @Et("l'ingénieur {string} est rattaché à l'entreprise {string}")
    public void ingenierRattacheAEntreprise(String email, String enterpriseName) {
        Engineer engineer = context.getEngineer(email);
        assertThat(engineer).isNotNull();
        assertThat(engineer.getEnterprise().getName()).isEqualTo(enterpriseName);
    }

    @Alors("l'entreprise {string} compte {int} ingénieurs rattachés")
    public void compterIngenieurs(String name, int count) {
        Enterprise enterprise = enterpriseService.findByName(name);
        assertThat(engineerRepository.countByEnterpriseId(enterprise.getId())).isEqualTo(count);
    }
}
