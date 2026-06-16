package com.backend.backend.bdd;

import com.backend.backend.metier.services.EnterpriseService;
import com.backend.backend.metier.services.TimeTrackingService;
import io.cucumber.java.fr.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class TempsStepDefinitions {

    @Autowired private TimeTrackingService timeTrackingService;
    @Autowired private EnterpriseService enterpriseService;
    @Autowired private BddContext context;

    private BigDecimal lastPeriodTotal;

    @Quand("l'ingénieur {string} enregistre {double} heures sur le rapport {string} le {string}")
    public void enregistrerTemps(String email, double hours, String reference, String date) {
        var engineer = context.getEngineer(email);
        if (engineer == null) {
            engineer = enterpriseService.findEngineerByEmail(email);
        }
        timeTrackingService.logTime(reference, engineer, LocalDate.parse(date), BigDecimal.valueOf(hours), null);
    }

    @Alors("le total d'heures sur le rapport {string} est de {double} heures")
    public void totalHeuresRapport(String reference, double expected) {
        assertThat(timeTrackingService.totalHoursOnReport(reference))
                .isEqualByComparingTo(BigDecimal.valueOf(expected));
    }

    @Etantdonné("que l'ingénieur {string} a enregistré {int} heures sur {string} le {string}")
    @Etantdonné("l'ingénieur {string} a enregistré {int} heures sur {string} le {string}")
    public void tempsDejaEnregistre(String email, int hours, String reference, String date) {
        enregistrerTemps(email, hours, reference, date);
    }

    @Quand("je calcule le total d'heures de {string} entre le {string} et le {string}")
    public void calculPeriode(String email, String start, String end) {
        var engineer = context.getEngineer(email);
        if (engineer == null) {
            engineer = enterpriseService.findEngineerByEmail(email);
        }
        lastPeriodTotal = timeTrackingService.totalHoursForEngineer(
                engineer, LocalDate.parse(start), LocalDate.parse(end));
    }

    @Alors("le total d'heures de {string} sur la période est de {int} heures")
    public void verifierTotalPeriode(String email, int expected) {
        assertThat(lastPeriodTotal).isEqualByComparingTo(BigDecimal.valueOf(expected));
    }
}
