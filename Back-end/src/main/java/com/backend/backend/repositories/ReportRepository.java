package com.backend.backend.repositories;

import com.backend.backend.entities.Report;
import com.backend.backend.entities.Engineer;
import com.backend.backend.Enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByReference(String reference);
    List<Report> findByAssignedEngineerAndStatus(Engineer engineer, ReportStatus status);
    List<Report> findByAssignedEngineer(Engineer engineer);
    List<Report> findByEnterpriseIdAndStatus(Long enterpriseId, ReportStatus status);
    List<Report> findByEnterpriseId(Long enterpriseId);
}
