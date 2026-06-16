package com.backend.backend.repositories;

import com.backend.backend.entities.ReportTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportTagRepository extends JpaRepository<ReportTag, Long> {
    Optional<ReportTag> findByEnterpriseIdAndLabel(Long enterpriseId, String label);
}
