package com.backend.backend.bdd;

import com.backend.backend.entities.Enterprise;
import com.backend.backend.entities.Engineer;
import com.backend.backend.entities.Report;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

/**
 * Contexte partagé entre les step definitions Cucumber.
 */
public class BddContext {

    private final Map<String, Enterprise> enterprises = new HashMap<>();
    private final Map<String, Engineer> engineers = new HashMap<>();
    private final Map<String, Report> reports = new HashMap<>();
    private Exception lastException;
    private Map<String, Long> kanbanCounts = new HashMap<>();
    private long dashboardStatusCount;
    private String dashboardStatus;

    public void reset() {
        enterprises.clear();
        engineers.clear();
        reports.clear();
        lastException = null;
        kanbanCounts.clear();
        dashboardStatusCount = 0;
        dashboardStatus = null;
    }

    public void storeEnterprise(String name, Enterprise enterprise) {
        enterprises.put(name, enterprise);
    }

    public Enterprise getEnterprise(String name) {
        return enterprises.get(name);
    }

    public void storeEngineer(String email, Engineer engineer) {
        engineers.put(email, engineer);
    }

    public Engineer getEngineer(String email) {
        return engineers.get(email);
    }

    public void storeReport(String reference, Report report) {
        reports.put(reference, report);
    }

    public Report getReport(String reference) {
        return reports.get(reference);
    }

    public Exception getLastException() { return lastException; }
    public void setLastException(Exception lastException) { this.lastException = lastException; }

    public Map<String, Long> getKanbanCounts() { return kanbanCounts; }
    public void setKanbanCounts(Map<String, Long> kanbanCounts) { this.kanbanCounts = kanbanCounts; }

    public long getDashboardStatusCount() { return dashboardStatusCount; }
    public void setDashboardStatusCount(long count) { this.dashboardStatusCount = count; }

    public String getDashboardStatus() { return dashboardStatus; }
    public void setDashboardStatus(String status) { this.dashboardStatus = status; }
}
