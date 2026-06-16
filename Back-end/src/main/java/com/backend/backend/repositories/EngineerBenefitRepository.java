package com.backend.backend.repositories;

import com.backend.backend.entities.EngineerBenefit;
import com.backend.backend.entities.Engineer;
import com.backend.backend.Enums.PeriodType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EngineerBenefitRepository extends JpaRepository<EngineerBenefit, Long> {
    Optional<EngineerBenefit> findByEngineerAndPeriodTypeAndYearAndPeriodNumber(
            Engineer engineer, PeriodType periodType, int year, int periodNumber);
    List<EngineerBenefit> findByEngineerEnterpriseIdAndPeriodTypeAndYear(
            Long enterpriseId, PeriodType periodType, int year);
}
