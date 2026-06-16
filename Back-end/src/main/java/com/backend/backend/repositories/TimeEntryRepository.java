package com.backend.backend.repositories;

import com.backend.backend.entities.TimeEntry;
import com.backend.backend.entities.Report;
import com.backend.backend.entities.Engineer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    List<TimeEntry> findByReport(Report report);
    List<TimeEntry> findByEngineer(Engineer engineer);

    @Query("SELECT COALESCE(SUM(t.hoursSpent), 0) FROM TimeEntry t WHERE t.report = :report")
    BigDecimal sumHoursByReport(@Param("report") Report report);

    @Query("SELECT COALESCE(SUM(t.hoursSpent), 0) FROM TimeEntry t " +
           "WHERE t.engineer = :engineer AND t.entryDate BETWEEN :start AND :end")
    BigDecimal sumHoursByEngineerBetween(@Param("engineer") Engineer engineer,
                                         @Param("start") LocalDate start,
                                         @Param("end") LocalDate end);
}
